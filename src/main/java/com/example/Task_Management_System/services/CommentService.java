package com.example.Task_Management_System.services;

import com.example.Task_Management_System.dto.CommentReq;
import com.example.Task_Management_System.dto.CommentResp;
import com.example.Task_Management_System.exception.CustomPermissionException;
import com.example.Task_Management_System.model.Comment;
import com.example.Task_Management_System.model.Task;
import com.example.Task_Management_System.model.User;
import com.example.Task_Management_System.repository.CommentRepository;
import com.example.Task_Management_System.repository.TaskRepository;
import com.example.Task_Management_System.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public Comment deleteOne(Long id, Authentication auth) {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no Comment with id: " + id));
        Long authorId = comment.getAuthorId();

        // Only User-Author can delete Comment
        Long userId = this.extractUserId(auth);
        boolean isAuthor = userId.equals(authorId);
        if (!isAuthor) {
            throw new CustomPermissionException("You have no permission to delete this comment: " + id);
        }

        repository.deleteById(id);

        return comment;    // for caching purpose only
    }

    // no need to evict cache for Users and Tasks because only comment's content can be changed
    public Long update(Long id, CommentReq newComment, Authentication auth) {

        Comment fromDB = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("There is no Comment with id: " + id));
        Long authorId = fromDB.getAuthorId();

        // Only User-Author can update Comment
        Long userId = this.extractUserId(auth);
        boolean isAuthor = userId.equals(authorId);
        if (!isAuthor) {
            throw new CustomPermissionException("You have no permission to update this comment: " + id);
        }

        fromDB.setContent(newComment.getContent());
        fromDB.setUpdated(new Date());

        return repository.save(fromDB).getId();
    }

    @Caching(evict = {
            @CacheEvict(value = "tasks", key = "#comment.taskId"),
            @CacheEvict(value = "user_resp", key = "#result.author.id")
    })
    public Comment create(CommentReq comment, Authentication auth) {
        // Only Logged User can create a Comment
        Long userId = this.extractUserId(auth);
        User author = userRepository.getReferenceById(userId);
        Task task = taskRepository.findById(comment.getTaskId()).orElseThrow(
                () -> new NoSuchElementException("There is no Task with taskId: " + comment.getTaskId())
        );
        Comment newComment = new Comment();
        newComment.setTask(task);
        newComment.setContent(comment.getContent());
        newComment.setAuthor(author);
        newComment.setCreated(new Date());

        return repository.saveAndFlush(newComment);
    }

    private Long extractUserId(Authentication auth) {
        User user = null;
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            user = (User) token.getPrincipal();
        }
        assert user != null;
        return user.getId();
    }

    public List<CommentResp> findAllByTaskId(Long taskId, Pageable pageable) {
        List<Comment> commentList = repository.findAllByTaskId(taskId, pageable);
        return commentList.stream().map(this::toResponse).toList();
    }

    public List<CommentResp> findAllByAuthorId(Long authorId, Pageable pageable) {
        List<Comment> commentList = repository.findAllByAuthorId(authorId, pageable);
        return commentList.stream().map(this::toResponse).toList();
    }

    public List<CommentResp> findAllBy(Pageable pageable) {
        List<Comment> commentList = repository.findAll(pageable).getContent();
        return commentList.stream().map(this::toResponse).toList();
    }

    private CommentResp toResponse(Comment comment) {
        CommentResp resp = new CommentResp();
        resp.setId(comment.getId());
        resp.setContent(comment.getContent());
        resp.setCreatedAt(this.toLocalDateTime(comment.getCreated()));
        resp.setUpdatedAt(this.toLocalDateTime(comment.getUpdated()));
        if (Hibernate.isInitialized(comment.getAuthor())) {
            User author = comment.getAuthor();
            resp.setAuthor("id: " + author.getId()
                    + ", name: " + author.getFirstName()
                    + ", surname: " + author.getLastName());
        } else {
            resp.setAuthor("id: " + comment.getAuthorId());
        }
        if (Hibernate.isInitialized(comment.getTask())) {
            Task task = comment.getTask();
            resp.setTask("id: " + task.getId()
                    + ", title: " + task.getTitle());
        } else {
            resp.setTask("id: " + comment.getTaskId());
        }

        return resp;
    }

    private String toLocalDateTime(Date date) {
        if (date == null) return "not updated";
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

}
