package com.ved.finzenz.finzenz.UserService.controller;
import com.ved.finzenz.finzenz.Configuration.JwtService;
import com.ved.finzenz.finzenz.UserService.dto.UserResponseDTO;
import com.ved.finzenz.finzenz.UserService.request.LoginRequest;
import com.ved.finzenz.finzenz.UserService.entity.User;
import com.ved.finzenz.finzenz.UserService.request.RegisterRequest;
import com.ved.finzenz.finzenz.UserService.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User APIs", description = "User registration and authentication using JWT")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    // ---------------- REGISTER ----------------
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns JWT access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequest request) {

        User user = userService.registerUser(request);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponseDTO.withTokens(user, accessToken, refreshToken));
    }

    // ---------------- LOGIN ----------------
    @Operation(
            summary = "Login user",
            description = "Authenticates user credentials and returns JWT access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequest request) {

        User user = userService.loginUser(request.getEmail(), request.getPassword());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                UserResponseDTO.withTokens(user, accessToken, refreshToken)
        );
    }
}