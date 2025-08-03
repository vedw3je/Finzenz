package com.ved.finzenz.finzenz.service;

import com.ved.finzenz.finzenz.entities.User;
import com.ved.finzenz.finzenz.exceptions.InvalidCredentialsException;
import com.ved.finzenz.finzenz.exceptions.UserAlreadyExistsException;
import com.ved.finzenz.finzenz.repository.UserRepository;
import com.ved.finzenz.finzenz.request.RegisterRequest;
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
        // 1. Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email already exists.");
        }

        // 2. Build User entity
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());

        // 3. Set default values
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());           // optional
        user.setUpdatedAt(LocalDateTime.now());           // optional
        user.setIsActive(true);                           // default flags
        user.setIsDeleted(false);
        user.setKycVerified(false);                       // or true if auto-verified
        user.setLastLoginAt(null);                        // initially null

        // 4. Save user
        return userRepository.save(user);
    }


    @Override
    public User loginUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
    }
}
