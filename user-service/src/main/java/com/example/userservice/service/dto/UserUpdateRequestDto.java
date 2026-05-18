package com.example.userservice.service.dto;

import com.example.userservice.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequestDto {
    private String fullName;
    private String phone;
    private String city;
    private String address;
    private Gender gender;
    private LocalDate birthday;
    private String password;
    private Boolean isReadPolicy;
}
