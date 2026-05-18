package com.example.userservice.service.dto;

import java.time.LocalDate;
import com.example.userservice.entity.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String email;
    private LocalDate birthday;
    private String phone;
    private String fullName;
    private Gender gender;
    private String city;
    private String address;
    private String roleName;
    private Boolean isPolicyRead = false;
    private Boolean emailVerified;
}
