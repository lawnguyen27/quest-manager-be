package com.example.missionservice.entity;

import com.example.common.enums.AchievementStatus;
import com.example.common.enums.AchievementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.common.entity.AbstractAuditingEntity;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "achievement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Achievement extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "achievement_status")
    private AchievementStatus achievementStatus;

    @Column(name = "points")
    private BigDecimal points;

    @OneToMany(mappedBy = "achievement")
    private List<UserAchievement> userAchievements;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AchievementType type;
}
