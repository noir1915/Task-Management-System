package com.example.Task_Management_System.dto;


import com.example.Task_Management_System.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // а нужен ли здесь Builder?
@AllArgsConstructor
@NoArgsConstructor
public class UserReq {

    @NotBlank
    @Size(min=3, max=20)
    private String firstName;
    @NotBlank
    @Size(min=3, max=20)
    private String lastName;
    @NotEmpty
    @Email
    private String email;
    @NotBlank
    @Size(min=3, max=20)
    private String password;
    private Role role;
}

