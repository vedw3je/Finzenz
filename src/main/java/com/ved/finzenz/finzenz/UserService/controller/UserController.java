package com.ved.finzenz.finzenz.UserService.controller;
import com.ved.finzenz.finzenz.UserService.dto.UserResponseDTO;
import com.ved.finzenz.finzenz.UserService.request.LoginRequest;
import com.ved.finzenz.finzenz.UserService.entity.User;
import com.ved.finzenz.finzenz.UserService.request.RegisterRequest;
import com.ved.finzenz.finzenz.UserService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequest request) {
        User user = userService.loginUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new UserResponseDTO(user));
    }
}