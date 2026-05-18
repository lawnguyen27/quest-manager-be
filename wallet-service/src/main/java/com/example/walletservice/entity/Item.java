package com.example.walletservice.entity;

import jakarta.persistence.*;
import com.example.common.entity.AbstractAuditingEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Item extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
}
