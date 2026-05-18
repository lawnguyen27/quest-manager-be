package com.example.missionservice.entity;

import com.example.common.enums.UserAchievementStatus;
import jakarta.persistence.*;
import lombok.*;
import com.example.common.entity.AbstractAuditingEntity;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_achievement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAchievement extends AbstractAuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserAchievementStatus status;

    @Column(name = "points")
    private BigDecimal points;

    @Builder.Default
    @Column(name = "points_awarded", nullable = false, columnDefinition = "boolean default false")
    private Boolean pointsAwarded = false;
}
