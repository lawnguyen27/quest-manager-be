package com.example.userservice.service;

import com.example.userservice.dto.BroadcastNotificationRequest;
import com.example.userservice.dto.CreateNotificationRequest;
import com.example.userservice.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createAndPushNotification(CreateNotificationRequest request);

    void createAndPushNotificationForAll(BroadcastNotificationRequest request);

    List<NotificationResponse> getUserNotifications(Long userId);

    void markAsRead(Long notificationId, Long userId);
}
