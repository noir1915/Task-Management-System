package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.model.Priority;
import com.example.Task_Management_System.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskReq {
    @NotBlank
    @Size(min = 3, max = 255)
    private String title;
    @NotBlank
    @Size(min = 3, max = 255)
    private String description;
    @NotNull
    private Status status;
    @NotNull
    private Priority priority;

    private Long executorId;
}
