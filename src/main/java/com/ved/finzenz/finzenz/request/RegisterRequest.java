package com.ved.finzenz.finzenz.request;

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
    private LocalDate dateOfBirth;

}
