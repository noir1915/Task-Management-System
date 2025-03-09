package com.example.Task_Management_System.services;

import com.example.Task_Management_System.model.User;
import com.example.Task_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByEmail(email);
        return userOptional.orElseThrow(
                () -> new UsernameNotFoundException("user not found with email :" + email));
    }
}
