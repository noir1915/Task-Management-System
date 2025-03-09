package com.example.Task_Management_System.controllers;

import com.example.Task_Management_System.dto.CommentReq;
import com.example.Task_Management_System.model.Comment;
import com.example.Task_Management_System.services.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")// вместо JWT Bearer
public class CommentController {
    private final CommentService service;

    @PostMapping("/create")
    public ResponseEntity<?> createdComment(CommentReq comment, Authentication authentication) {
        Comment createComment = service.create(comment, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created with id: " + createComment.getId());
    }
}
