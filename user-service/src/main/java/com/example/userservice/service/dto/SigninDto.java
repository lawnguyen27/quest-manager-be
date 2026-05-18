package com.example.userservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninDto {
	private UserDto user;
	private String accessToken;
	private String refreshToken;
	private String message;
}
