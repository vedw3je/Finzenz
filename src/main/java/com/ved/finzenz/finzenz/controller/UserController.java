package com.ved.finzenz.finzenz.controller;
import com.ved.finzenz.finzenz.dto.UserResponseDTO;
import com.ved.finzenz.finzenz.request.LoginRequest;
import com.ved.finzenz.finzenz.entities.User;
import com.ved.finzenz.finzenz.request.RegisterRequest;
import com.ved.finzenz.finzenz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
            User savedUser = userService.registerUser(request);
            return ResponseEntity.ok(new UserResponseDTO(savedUser));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
            User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new UserResponseDTO(user));
    }


}
