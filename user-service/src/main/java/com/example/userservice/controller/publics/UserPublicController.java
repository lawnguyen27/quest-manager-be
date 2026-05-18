package com.example.userservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.userservice.service.dto.UserDto;
import com.example.userservice.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.userservice.service.dto.UserUpdateRequestDto;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/public/users")
@AllArgsConstructor
public class UserPublicController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserDto> getMe() {
        return ApiResponse.success(userService.getMe());
    }

    @PatchMapping("/me")
    public ApiResponse<UserDto> updateMe(@RequestBody UserUpdateRequestDto body) {
        return ApiResponse.success(userService.editProfile(body));
    }

    @PatchMapping("/me/policy")
    public ApiResponse<UserDto> updateReadPolicy(@RequestBody UserUpdateRequestDto body) {
        return ApiResponse.success(userService.editReadPolicy(body));
    }
}
