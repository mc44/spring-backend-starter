package com.mfajardo.spring_backend_starter.dto;


import com.mfajardo.spring_backend_starter.entity.enums.Gender;
import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String email;
    private String password;
    private Gender gender;
}
