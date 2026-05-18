package com.example.userservice.entity;

import jakarta.persistence.*;
import com.example.common.entity.AbstractAuditingEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String type; // e.g., CUSTOM, MISSION

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;
}
