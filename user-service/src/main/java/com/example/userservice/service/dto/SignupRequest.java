package com.example.userservice.service.dto;

import java.time.LocalDate;

import com.example.userservice.entity.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String pass;

	@NotNull
	private LocalDate birthday;

	private String phone;
	private String fullname;

	private Gender gender;

	private String city;
	private String address;
}
