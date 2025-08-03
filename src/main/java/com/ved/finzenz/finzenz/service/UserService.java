package com.ved.finzenz.finzenz.service;


import com.ved.finzenz.finzenz.entities.User;
import com.ved.finzenz.finzenz.request.RegisterRequest;

public interface UserService {
    User registerUser(RegisterRequest request);
    User loginUser(String email, String password);
}

