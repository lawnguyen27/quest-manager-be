package com.example.missionservice.controller.privates;

import com.example.common.dto.ApiResponse;
import com.example.common.dto.UserMissionDto;
import com.example.missionservice.service.UserMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/user-missions")
@RequiredArgsConstructor
public class UserMissionPrivateController {

    private final UserMissionService userMissionService;

    @GetMapping
    public ApiResponse<Page<UserMissionDto>> getAllUserMissions(Pageable pageable) {
        return ApiResponse.success(userMissionService.getAllUserMissions(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserMissionDto> getUserMissionById(@PathVariable Long id) {
        return ApiResponse.success(userMissionService.getUserMissionById(id));
    }
}
