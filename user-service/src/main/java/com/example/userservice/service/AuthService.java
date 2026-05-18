package com.example.userservice.service;

import com.example.userservice.service.dto.SigninDto;
import com.example.userservice.service.dto.SigninRequest;
import com.example.userservice.service.dto.SignupDto;
import com.example.userservice.service.dto.SignupRequest;

public interface AuthService {
    SignupDto signUp(SignupRequest request);
    SigninDto signIn(SigninRequest request);
    void logout(String token);
    boolean isTokenBlacklisted(String token);
    SigninDto refresh(String refreshToken);
    void sendVerificationEmail(String email);
    void verifyEmail(String email, String code);
}
