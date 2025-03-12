package com.example.Task_Management_System.services;

import com.example.Task_Management_System.dto.AuthRequest;
import com.example.Task_Management_System.dto.AuthResponse;
import com.example.Task_Management_System.dto.UserReq;
import com.example.Task_Management_System.dto.UserResp;
import com.example.Task_Management_System.exception.ResourceNotFoundException;
import com.example.Task_Management_System.exception.UserAlreadyExistsException;
import com.example.Task_Management_System.model.Comment;
import com.example.Task_Management_System.model.Task;
import com.example.Task_Management_System.model.User;
import com.example.Task_Management_System.repository.TaskRepository;
import com.example.Task_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final TaskRepository taskRepository;

    private final AuthenticationManager authenticationManager;

    public Long register(UserReq userReq) {
        if (repository.findUserByEmail(userReq.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("ERROR: Email already registered: " + userReq.getEmail());
        }

        User user = mapDtoToUser(userReq);
        User savedUser = repository.save(user);
        return savedUser.getId();
    }

    public AuthResponse login(AuthRequest authRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                ));
        User user = (User) auth.getPrincipal();
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(user.getId(), token);
    }

    public List<UserResp> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // we need 3 calls here to avoid MultipleBagFetchException and Cartesian product
    @Cacheable(value = "user_resp", key = "#id")
    @Transactional(readOnly = true)
    public UserResp getById(Long id) {
        User user = repository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("There is no User with id: " + id));
        return toResponse(user);
    }

    public UserResp toResponse(User user) {
        UserResp resp = new UserResp();
        resp.setId(user.getId());
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole());
        //Выделение методов: Созданы два вспомогательных метода getTaskList и getCommentList.
        // Эти методы принимают список задач или комментариев и сообщение для случая, когда данные загружаются лениво.
        resp.setAsAuthor(getTaskList(user.getAuthoredTasks(), "undefined: loaded lazily"));
        resp.setAsExecutor(getTaskList(user.getExecutedTasks(), "undefined: loaded lazily"));
        resp.setComments(getCommentList(user.getComments(), "undefined: loaded lazily"));

        return resp;
    }

    private List<String> getTaskList(List<Task> tasks, String lazyMessage) {
        if (Hibernate.isInitialized(tasks)) {
            return tasks.stream()
                    .map(t -> "id: " + t.getId() + ", title: " + t.getTitle())
                    .toList();
        } else {
            return List.of(lazyMessage);
        }
    }

    private List<String> getCommentList(List<Comment> comments, String lazyMessage) {
        if (Hibernate.isInitialized(comments)) {
            return comments.stream()
                    .map(c -> "id: " + c.getId() + ", to task: " + c.getTaskId())
                    .toList();
        } else {
            return List.of(lazyMessage);
        }
    }

    public User mapDtoToUser(UserReq userReq) {
        return User.builder()
                .firstName(userReq.getFirstName())
                .lastName(userReq.getLastName())
                .email(userReq.getEmail())
                .password(encodePassword(userReq.getPassword()))
                .role(userReq.getRole())  // или установить постоянную роль, например "USER"
                .build();
    }

    @Caching(evict = {
            @CacheEvict(value = "users", key = "#result.email"),
            @CacheEvict(value = "user_resp", key = "#userId"),
            @CacheEvict(value = "tasks", allEntries = true)
    })
    public User deleteUser(Long userId) {
        User user = findUserById(userId);
        clearTaskExecutors(user.getExecutedTasks());
        repository.delete(user);
        return user; // для целей кэширования
    }

    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user_resp", key = "#result.id")
    })
    public UserResp updateUser(UserReq newUser, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        User userToUpdate = findUserById(currentUser.getId());

        updateUserDetails(userToUpdate, newUser);

        User savedUser = repository.save(userToUpdate);
        return this.toResponse(savedUser);
    }

    // Вспомогательные методы
    private String encodePassword(String password) {
        return encoder.encode(password);
    }

    private User findUserById(Long userId) {
        return repository.findByIdWithTasks(userId).orElseThrow(
                () -> new NoSuchElementException("There is no User with id: " + userId));
    }

    private void clearTaskExecutors(List<Task> tasks) {
        List<Long> executedTaskList = tasks.stream()
                .map(Task::getId)
                .toList();
        taskRepository.clearExecutors(executedTaskList);
    }

    private void updateUserDetails(User user, UserReq newUser) {
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPassword(encodePassword(newUser.getPassword()));
        user.setRole(newUser.getRole());
    }
}
