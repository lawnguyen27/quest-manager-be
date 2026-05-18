package com.example.walletservice.entity;

import jakarta.persistence.*;
import com.example.common.entity.AbstractAuditingEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "user_wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserWallet extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "points", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal points = BigDecimal.ZERO;
}
