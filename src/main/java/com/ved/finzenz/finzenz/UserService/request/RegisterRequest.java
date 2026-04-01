package com.ved.finzenz.finzenz.UserService.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ved.finzenz.finzenz.UserService.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String gender;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
}
