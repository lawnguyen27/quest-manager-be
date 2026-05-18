package com.example.walletservice.service;
import com.example.walletservice.dto.UserWalletDto;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    void addPoints(Long userId, BigDecimal points);
    void deductPoints(Long userId, BigDecimal points);
    BigDecimal getBalance(Long userId);
    List<UserWalletDto> getAllWallets();
    UserWalletDto getWalletByUserId(Long userId);
    UserWalletDto createWallet(Long userId);
}
