package com.ved.finzenz.finzenz.UserService.service;

import com.ved.finzenz.finzenz.UserService.entity.User;
import com.ved.finzenz.finzenz.UserService.enums.Role;
import com.ved.finzenz.finzenz.exceptions.InvalidCredentialsException;
import com.ved.finzenz.finzenz.exceptions.UserAlreadyExistsException;
import com.ved.finzenz.finzenz.UserService.repository.UserRepository;
import com.ved.finzenz.finzenz.UserService.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email already exists.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Defaults
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setIsDeleted(false);
        user.setKycVerified(false);

        return userRepository.save(user);
    }


    @Override
    public User loginUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("User account is inactive");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return user;
    }
}
