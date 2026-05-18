package com.example.userservice.service.impl;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.example.common.api.mission.MissionClient;
import com.example.userservice.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.example.userservice.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.userservice.service.dto.SigninRequest;
import com.example.userservice.service.dto.SigninDto;
import com.example.userservice.service.dto.SignupRequest;
import com.example.userservice.service.dto.SignupDto;
import com.example.userservice.service.dto.UserDto;
import com.example.common.config.exception.BadRequestException;
import com.example.common.config.security.JwtService;
import com.example.common.config.exception.NotFoundException;
import com.example.userservice.constants.MessageCodeConstants;
import com.example.userservice.entity.User;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;

import static com.example.userservice.constants.MessageCodeConstants.EMAIL_ALREADY_VERIFIED;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final StringRedisTemplate redisTemplate;
	private final MailService mailService;
	private final MissionClient missionClient;

	private static final String BLACKLIST_PREFIX = "JWT_BLACKLIST:";
	private static final String ACTIVE_TOKEN_PREFIX = "USER_ACTIVE_TOKEN:";

	@Transactional
	@Override
	public SignupDto signUp(SignupRequest request) {
		String email = normalizeEmail(request.getEmail());

		if (userRepository.existsByEmail(email)) {
			throw new BadRequestException(MessageCodeConstants.EMAIL_ALREADY_EXISTS);
		}

		User user = new User();
		user.setEmail(email);
		user.setPass(passwordEncoder.encode(request.getPass()));
		user.setBirthday(request.getBirthday());
		user.setPhone(request.getPhone());
		user.setFullname(request.getFullname());
		user.setGender(request.getGender());
		user.setCity(request.getCity());
		user.setAddress(request.getAddress());
        user.setCreatedBy("SYSTEM");

		var customerRole = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new BadRequestException(MessageCodeConstants.ROLE_NOT_FOUND));
		user.setRole(customerRole);

		userRepository.save(user);
		return new SignupDto(user.getId(), user.getEmail(), MessageCodeConstants.SIGNUP_SUCCESS);
	}

	@Override
	public SigninDto signIn(SigninRequest request) {
		String email = normalizeEmail(request.getEmail());

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException(MessageCodeConstants.USER_NOT_FOUND));

		boolean passwordMatches = passwordEncoder.matches(request.getPass(), user.getPass());
		if (!passwordMatches) {
			throw new BadRequestException(MessageCodeConstants.INVALID_CREDENTIALS);
		}

		String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().getName());

		String oldToken = getActiveToken(user.getId());
		if (oldToken != null) {
			long remaining = jwtService.getRemainingExpiration(oldToken);
			blacklistToken(oldToken, remaining);
		}

		setActiveToken(user.getId(), token, jwtService.getRemainingExpiration(token));
		
		// Initialize Achievements (awards Login achievement and sets others to PROCESSING)
		try {
			missionClient.initAchievements(user.getId());
		} catch (Exception e) {
			log.error("Failed to initialize achievements for userId: {}. Error: {}", user.getId(), e.getMessage());
		}

		UserDto userDto = mapToUserDto(user);

		return new SigninDto(userDto, token, token, MessageCodeConstants.SIGNIN_SUCCESS);
	}

	@Override
	public SigninDto refresh(String refreshToken) {
		if (!jwtService.isTokenValid(refreshToken) || isTokenBlacklisted(refreshToken)) {
			throw new BadRequestException(MessageCodeConstants.INVALID_REFRESH_TOKEN);
		}

		String email = jwtService.extractAllClaims(refreshToken).getSubject();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException(MessageCodeConstants.USER_NOT_FOUND));

		String newToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().getName());
		
		long remaining = jwtService.getRemainingExpiration(refreshToken);
		blacklistToken(refreshToken, remaining);
		setActiveToken(user.getId(), newToken, jwtService.getRemainingExpiration(newToken));

		return new SigninDto(mapToUserDto(user), newToken, newToken, "Token refreshed successfully");
	}

	private UserDto mapToUserDto(User user) {
		return UserDto.builder()
				.id(user.getId())
				.email(user.getEmail())
				.birthday(user.getBirthday())
				.phone(user.getPhone())
				.fullName(user.getFullname())
				.gender(user.getGender())
				.city(user.getCity())
				.address(user.getAddress())
				.roleName(user.getRole() != null ? user.getRole().getName() : null)
				.emailVerified(user.isEmailVerified())
				.build();
	}

	@Override
	public void logout(String token) {
		long remaining = jwtService.getRemainingExpiration(token);
		blacklistToken(token, remaining);
	}

	@Override
	public boolean isTokenBlacklisted(String token) {
		return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
	}

	private void blacklistToken(String token, long expirationInMs) {
		if (expirationInMs > 0) {
			redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", expirationInMs, TimeUnit.MILLISECONDS);
		}
	}

	private void setActiveToken(Long userId, String token, long expirationInMs) {
		if (expirationInMs > 0) {
			redisTemplate.opsForValue().set(ACTIVE_TOKEN_PREFIX + userId, token, expirationInMs, TimeUnit.MILLISECONDS);
		}
	}

	private String getActiveToken(Long userId) {
		return redisTemplate.opsForValue().get(ACTIVE_TOKEN_PREFIX + userId);
	}

	private String normalizeEmail(String email) {
		return Objects.requireNonNull(email).trim().toLowerCase();
	}

	private static final String EMAIL_VERIFICATION_PREFIX = "EMAIL_VERIFICATION:";

	@Override
	public void sendVerificationEmail(String email) {
		User user = userRepository.findByEmail(normalizeEmail(email))
				.orElseThrow(() -> new NotFoundException(MessageCodeConstants.USER_NOT_FOUND));

		String code = String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000));
		
		redisTemplate.opsForValue().set(EMAIL_VERIFICATION_PREFIX + user.getEmail(), code, 15, TimeUnit.MINUTES);
		mailService.sendVerificationEmail(user.getEmail(), code);
	}

	@Override
	@Transactional
	public void verifyEmail(String email, String code) {
		String normalizedEmail = normalizeEmail(email);
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new NotFoundException(MessageCodeConstants.USER_NOT_FOUND));

		if (user.isEmailVerified()) {
			throw new BadRequestException(EMAIL_ALREADY_VERIFIED);
		}

		String savedCode = redisTemplate.opsForValue().get(EMAIL_VERIFICATION_PREFIX + normalizedEmail);
		if (savedCode == null || !savedCode.equals(code)) {
			throw new BadRequestException("Invalid or expired verification code");
		}

		user.setEmailVerified(true);
		userRepository.save(user);
		redisTemplate.delete(EMAIL_VERIFICATION_PREFIX + normalizedEmail);

		try {
			missionClient.awardAchievement(user.getId(), com.example.common.enums.AchievementType.VERIFY_EMAIL);
		} catch (Exception e) {
            log.error("Failed to award achievement for email verification: {}", e.getMessage());
        }
	}
}
