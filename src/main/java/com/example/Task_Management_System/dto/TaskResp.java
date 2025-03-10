package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.model.Task;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class TaskResp {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private String author;
    private String executor;
    private String comments;
    private String createdAt;
    private String updatedAt;
}
