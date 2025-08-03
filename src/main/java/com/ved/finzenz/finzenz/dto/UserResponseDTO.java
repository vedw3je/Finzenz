package com.ved.finzenz.finzenz.dto;

import com.ved.finzenz.finzenz.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private boolean isActive;
    private boolean kycVerified;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.gender = user.getGender();
        this.dateOfBirth = user.getDateOfBirth();
        this.isActive = user.getIsActive();
        this.kycVerified = user.getKycVerified();
    }
}

