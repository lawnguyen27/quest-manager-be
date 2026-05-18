package com.example.userservice.service;

import com.example.userservice.service.dto.UserDto;
import com.example.userservice.service.dto.UserUpdateRequestDto;
import com.example.userservice.service.specification.UserCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDto> getAllUsers(UserCriteria criteria, Pageable pageable);
    UserDto getUserById(Long id);
    UserDto getMe();
    UserDto editProfile(UserUpdateRequestDto userUpdateRequestDto);
    UserDto editReadPolicy(UserUpdateRequestDto userUpdateRequestDto);
}
