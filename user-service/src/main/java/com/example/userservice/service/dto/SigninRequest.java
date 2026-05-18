package com.example.userservice.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninRequest {
	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String pass;
}
