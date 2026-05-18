package com.example.userservice.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

import com.example.common.entity.AbstractAuditingEntity;
import com.example.userservice.entity.enums.Gender;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends AbstractAuditingEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email")
	private String email;

	@Column(name = "pass")
	private String pass;

	@Column(name = "birthday")
	private LocalDate birthday;

	@Column(name = "phone")
	private String phone;

	@Column(name = "fullname")
	private String fullname;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@Column(name = "city")
	private String city;

	@Column(name = "address")
	private String address;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private boolean isEmailVerified = false;

    @Column(name = "is_policy_read", nullable = false)
    @Builder.Default
    private boolean isPolicyRead = false;
}
