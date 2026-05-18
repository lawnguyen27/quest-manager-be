package com.example.userservice.controller.internal;

import com.example.common.dto.NotificationRequestDto;
import com.example.userservice.dto.CreateNotificationRequest;
import com.example.userservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService notificationService;

    @PostMapping("/push")
    public ResponseEntity<Void> pushNotification(@RequestBody NotificationRequestDto request) {
        CreateNotificationRequest createReq = CreateNotificationRequest.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .build();
        notificationService.createAndPushNotification(createReq);
        return ResponseEntity.ok().build();
    }
}
