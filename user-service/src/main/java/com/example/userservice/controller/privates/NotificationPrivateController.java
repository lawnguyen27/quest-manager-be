package com.example.userservice.controller.privates;

import com.example.userservice.dto.BroadcastNotificationRequest;
import com.example.userservice.dto.CreateNotificationRequest;
import com.example.userservice.dto.NotificationResponse;
import com.example.userservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NotificationPrivateController {

    private final NotificationService notificationService;

    @PostMapping("/push")
    public ResponseEntity<NotificationResponse> pushNotification(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.createAndPushNotification(request));
    }

    @PostMapping("/push/all")
    public ResponseEntity<Void> pushNotificationForAll(@Valid @RequestBody BroadcastNotificationRequest request) {
        notificationService.createAndPushNotificationForAll(request);
        return ResponseEntity.ok().build();
    }
}
