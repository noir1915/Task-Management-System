package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.model.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserResp {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private List<String> asAuthor;
    private List<String> asExecutor;
    private List<String> comments;
}
