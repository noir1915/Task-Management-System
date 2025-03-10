package com.example.Task_Management_System.repository;

import com.example.Task_Management_System.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author", "task"})
    List<Comment> findAllByTaskId(Long taskId, Pageable pageable);


    @EntityGraph(attributePaths = {"author", "task"})
    List<Comment> findAllByAuthorId(Long authorId, Pageable pageable);
}
