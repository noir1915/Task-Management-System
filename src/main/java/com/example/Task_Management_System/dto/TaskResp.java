package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.model.Priority;
import com.example.Task_Management_System.model.Status;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class TaskResp {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String author;
    private String executor;
    private String comments;
    private String createdAt;
    private String updatedAt;
}
