package com.example.walletservice.service.impl;

import com.example.common.config.exception.BadRequestException;
import com.example.walletservice.dto.UserWalletDto;
import com.example.walletservice.entity.UserWallet;
import com.example.walletservice.repository.UserWalletRepository;
import com.example.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.walletservice.constants.MessageCodeConstants.INSUFFICIENT_BALANCE;
import static com.example.walletservice.constants.MessageCodeConstants.USER_WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {

    private final UserWalletRepository userWalletRepository;

    @Override
    @Transactional
    public void addPoints(Long userId, BigDecimal points) {
        UserWallet wallet = userWalletRepository.lockById(userId)
                .orElseGet(() -> UserWallet.builder().userId(userId).build());
        wallet.setPoints(wallet.getPoints().add(points));
        userWalletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void deductPoints(Long userId, BigDecimal points) {
        UserWallet wallet = userWalletRepository.lockById(userId)
                .orElseThrow(() -> new BadRequestException(USER_WALLET_NOT_FOUND));
        if (wallet.getPoints().compareTo(points) < 0) {
            throw new BadRequestException(INSUFFICIENT_BALANCE);
        }
        wallet.setPoints(wallet.getPoints().subtract(points));
        userWalletRepository.save(wallet);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        return userWalletRepository.findByUserId(userId)
                .map(UserWallet::getPoints)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserWalletDto> getAllWallets() {
        return userWalletRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserWalletDto getWalletByUserId(Long userId) {
        return userWalletRepository.findByUserId(userId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Override
    public UserWalletDto createWallet(Long userId) {
        UserWallet wallet = userWalletRepository.findByUserId(userId).orElse(null);
        if (wallet == null) {
            wallet = UserWallet.builder()
                    .userId(userId)
                    .build();
            return mapToResponse(userWalletRepository.save(wallet));
        } else {
            return mapToResponse(wallet);
        }
    }

    private UserWalletDto mapToResponse(UserWallet wallet) {
        return UserWalletDto.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .points(wallet.getPoints())
                .createdAt(wallet.getCreatedDate())
                .updatedAt(wallet.getLastModifiedDate())
                .build();
    }
}
