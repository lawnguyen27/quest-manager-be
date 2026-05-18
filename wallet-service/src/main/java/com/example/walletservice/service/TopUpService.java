package com.example.walletservice.service;

import com.example.walletservice.dto.TopUpPackageDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface TopUpService {

    List<TopUpPackageDto> getAvailablePackages();

    String createPaymentUrl(Long userId, String packageCode, HttpServletRequest request);

    String processPaymentCallback(Map<String, String> params);
    
    void processTransactionAsync(String txnRef, String responseCode);
    
    void addPointsToUserWallet(Long userId, java.math.BigDecimal points);
}
