package com.example.userservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.dto.SigninDto;
import com.example.userservice.service.dto.SigninRequest;
import com.example.userservice.service.dto.SignupDto;
import com.example.userservice.service.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
public class AuthPublicController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<SignupDto> signUp(@RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signUp(request));
    }

    @PostMapping("/signin")
    public ApiResponse<SigninDto> signIn(@RequestBody SigninRequest request) {
        return ApiResponse.success(authService.signIn(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<SigninDto> refresh(@RequestParam String refreshToken) {
        return ApiResponse.success(authService.refresh(refreshToken));
    }

    @PostMapping("/send-verification-email")
    public ApiResponse<Void> sendVerificationEmail(@RequestParam String email) {
        authService.sendVerificationEmail(email);
        return ApiResponse.success(null);
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String email, @RequestParam String code) {
        authService.verifyEmail(email, code);
        return ApiResponse.success(null);
    }
}
