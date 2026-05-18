package com.example.userservice.controller.commons;

import com.example.common.config.exception.BadRequestException;
import com.example.common.dto.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.service.dto.SigninRequest;
import com.example.userservice.service.dto.SigninDto;
import com.example.userservice.service.dto.SignupRequest;
import com.example.userservice.service.dto.SignupDto;
import com.example.userservice.service.AuthService;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import java.util.Map;

import static com.example.userservice.constants.MessageCodeConstants.REFRESH_TOKEN_IS_EMPTY;

@RestController
@RequestMapping("/api/common/auth")
@Validated
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<SignupDto> signUp(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signUp(request));
    }

    @PostMapping("/signin")
    public ApiResponse<SigninDto> signIn(@Valid @RequestBody SigninRequest request) {
        return ApiResponse.success(authService.signIn(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        return ApiResponse.success(null);
    }

    @PostMapping("/refresh")
    public ApiResponse<SigninDto> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            throw new BadRequestException(REFRESH_TOKEN_IS_EMPTY);
        }
        return ApiResponse.success(authService.refresh(refreshToken));
    }
}
