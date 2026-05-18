package com.example.missionservice.entity;

import com.example.common.enums.MissionStatus;
import com.example.common.enums.MissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.common.entity.AbstractAuditingEntity;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "mission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Mission extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type")
    private MissionType missionType;

    @Column(name = "document_link")
    private String documentLink;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @Column(name = "from_date")
    private Instant fromDate;

    @Column(name = "to_date")
    private Instant toDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status")
    private MissionStatus missionStatus;

    @Column(name = "points")
    private BigDecimal points;

    @OneToMany(mappedBy = "mission")
    private List<UserMission> userMissions;
}
