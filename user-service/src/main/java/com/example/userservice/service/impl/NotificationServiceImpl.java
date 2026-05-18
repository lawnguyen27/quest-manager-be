package com.example.userservice.service.impl;

import com.example.common.config.exception.NotFoundException;
import com.example.userservice.dto.BroadcastNotificationRequest;
import com.example.userservice.dto.CreateNotificationRequest;
import com.example.userservice.dto.NotificationResponse;
import com.example.userservice.entity.Notification;
import com.example.userservice.entity.User;
import com.example.userservice.repository.NotificationRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    /** Broadcast only to ROLE_USER (roles.id = 2 in seed data). */
    private static final long ROLE_USER_ID = 2L;

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public NotificationResponse createAndPushNotification(CreateNotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);

        messagingTemplate.convertAndSendToUser(
                request.getUserId().toString(),
                "/queue/notifications",
                response
        );

        return response;
    }

    @Override
    @Transactional
    public void createAndPushNotificationForAll(BroadcastNotificationRequest request) {
        List<User> users = userRepository.findByRole_Id(ROLE_USER_ID);
        if (users.isEmpty()) {
            log.warn("No users with ROLE_USER (role_id={}); broadcast skipped", ROLE_USER_ID);
            return;
        }
        for (User user : users) {
            Notification notification = Notification.builder()
                    .userId(user.getId())
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .type(request.getType())
                    .isRead(false)
                    .build();
            Notification saved = notificationRepository.save(notification);
            NotificationResponse response = mapToResponse(saved);
            messagingTemplate.convertAndSendToUser(
                    user.getId().toString(),
                    "/queue/notifications",
                    response
            );
        }
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!notification.getUserId().equals(userId)) {
            throw new NotFoundException("Notification not found");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdDate(notification.getCreatedDate())
                .build();
    }
}
