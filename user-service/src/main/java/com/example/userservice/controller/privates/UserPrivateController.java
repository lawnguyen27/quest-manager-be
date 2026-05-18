package com.example.userservice.controller.privates;

import java.util.List;

import com.example.userservice.service.specification.UserCriteria;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.ApiResponse;
import com.example.userservice.service.dto.UserDto;
import com.example.userservice.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/private/users")
@AllArgsConstructor
public class UserPrivateController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserDto>> getAllUsers(@ParameterObject UserCriteria criteria,
                                                   @ParameterObject @PageableDefault Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(criteria, pageable);
        return ApiResponse.success(users.getContent());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }
}
