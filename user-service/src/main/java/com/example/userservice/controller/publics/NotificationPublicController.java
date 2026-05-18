package com.example.userservice.controller.publics;

import com.example.userservice.dto.NotificationResponse;
import com.example.userservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/notifications")
@RequiredArgsConstructor
public class NotificationPublicController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }
}
