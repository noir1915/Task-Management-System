package com.example.Task_Management_System.repository;

import com.example.Task_Management_System.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //Не добавил EntityGraph
    List<Comment> findAllByTaskId(Long taskId, Pageable pageable);

    // Не добивил EntityGraph
    List<Comment> findAllByAuthorId(Long authorId, Pageable pageable);
}
