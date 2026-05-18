package com.example.userservice.service.impl;

import com.example.common.api.mission.MissionClient;
import com.example.common.config.exception.BadRequestException;
import com.example.common.enums.AchievementType;
import com.example.userservice.service.UserService;
import com.example.userservice.service.dto.UserUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.common.config.exception.NotFoundException;
import com.example.userservice.entity.User;
import com.example.userservice.entity.Role;
import com.example.userservice.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import com.example.userservice.entity.User_;
import com.example.userservice.entity.Role_;
import com.example.common.config.query.QueryService;
import com.example.userservice.service.specification.UserCriteria;
import com.example.userservice.service.dto.UserDto;

import static com.example.userservice.constants.MessageCodeConstants.USER_NOT_FOUND;

@Slf4j
@Service
public class UserServiceImpl extends QueryService<User> implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MissionClient missionClient;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MissionClient missionClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.missionClient = missionClient;
    }

    @Override
    public Page<UserDto> getAllUsers(UserCriteria criteria, Pageable pageable) {
        return userRepository.findAll(createSpecification(criteria), pageable).map(this::mapToUserDto);
    }

    protected Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = (root, query, cb) -> cb.conjunction();
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), User_.id));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), User_.email));
            }
            if (criteria.getBirthday() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBirthday(), User_.birthday));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), User_.phone));
            }
            if (criteria.getFullname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullname(), User_.fullname));
            }
            if (criteria.getGender() != null) {
                specification = specification.and(buildSpecification(criteria.getGender(), User_.gender));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), User_.city));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), User_.address));
            }
            if (criteria.getRoleName() != null) {
                specification = specification.and(buildSpecificationByFunction(criteria.getRoleName(), 
                    root -> root.get(User_.role).get(Role_.name)));
            }
        }
        return specification;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        return mapToUserDto(user);
    }

    @Override
    public UserDto getMe() {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        return mapToUserDto(user);
    }

    @Override
    public UserDto editProfile(UserUpdateRequestDto userUpdateRequestDto) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        if (userUpdateRequestDto.getFullName() != null) {
            user.setFullname(userUpdateRequestDto.getFullName());
        }
        if (userUpdateRequestDto.getBirthday() != null) {
            user.setBirthday(userUpdateRequestDto.getBirthday());
        }
        if (userUpdateRequestDto.getGender() != null) {
            user.setGender(userUpdateRequestDto.getGender());
        }
        if (userUpdateRequestDto.getCity() != null) {
            user.setCity(userUpdateRequestDto.getCity());
        }
        if (userUpdateRequestDto.getAddress() != null) {
            user.setAddress(userUpdateRequestDto.getAddress());
        }
        if (userUpdateRequestDto.getPhone() != null) {
            user.setPhone(userUpdateRequestDto.getPhone());
        }
        if (userUpdateRequestDto.getPassword() != null && !userUpdateRequestDto.getPassword().isBlank()) {
            user.setPass(passwordEncoder.encode(userUpdateRequestDto.getPassword()));
        }
        return mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto editReadPolicy(UserUpdateRequestDto userUpdateRequestDto) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        if (user.isPolicyRead()) {
            throw new BadRequestException("Policy has already been read");
        } else {
            user.setPolicyRead(userUpdateRequestDto.getIsReadPolicy());
            User savedUser = userRepository.save(user);
            if (savedUser.isPolicyRead()) {
                try {
                    missionClient.awardAchievement(userId, AchievementType.POLICY);
                } catch (Exception e) {
                    log.error("Failed to award achievement for policy: {}", e.getMessage());
                }
            }
            return mapToUserDto(savedUser);
        }
    }

    private UserDto mapToUserDto(User user) {
        UserDto response = new UserDto();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setBirthday(user.getBirthday());
        response.setPhone(user.getPhone());
        response.setFullName(user.getFullname());
        response.setGender(user.getGender());
        response.setCity(user.getCity());
        response.setAddress(user.getAddress());
        if (user.getRole() != null) {
            response.setRoleName(user.getRole().getName());
        }
        response.setIsPolicyRead(user.isPolicyRead());
        response.setEmailVerified(user.isEmailVerified());
        return response;
    }
}
