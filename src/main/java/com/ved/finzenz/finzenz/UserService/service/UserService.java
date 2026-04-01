package com.ved.finzenz.finzenz.UserService.service;


import com.ved.finzenz.finzenz.UserService.entity.User;
import com.ved.finzenz.finzenz.UserService.request.RegisterRequest;

public interface UserService {
    User registerUser(RegisterRequest request);
    User loginUser(String email, String password);
}

