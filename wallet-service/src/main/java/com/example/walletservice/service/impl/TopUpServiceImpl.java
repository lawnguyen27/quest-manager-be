package com.example.walletservice.service.impl;

import com.example.common.config.exception.BadRequestException;
import com.example.walletservice.config.VNPayConfig;
import com.example.walletservice.dto.TopUpPackageDto;
import com.example.walletservice.entity.Transaction;
import com.example.walletservice.entity.UserWallet;
import com.example.walletservice.entity.TopUpPackage;
import com.example.walletservice.enums.TransactionStatus;
import com.example.walletservice.repository.TopUpPackageRepository;
import com.example.walletservice.repository.TransactionRepository;
import com.example.walletservice.repository.UserWalletRepository;
import com.example.walletservice.service.TopUpService;
import com.example.walletservice.util.VNPayUtil;
import com.example.walletservice.event.VNPayTransactionEvent;
import com.example.common.constants.KafkaTopicConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopUpServiceImpl implements TopUpService {

    private final TransactionRepository transactionRepository;
    private final UserWalletRepository userWalletRepository;
    private final TopUpPackageRepository topUpPackageRepository;
    private final VNPayConfig vnPayConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public List<TopUpPackageDto> getAvailablePackages() {
        return topUpPackageRepository.findAllByIsActiveTrue().stream()
                .map(pkg -> TopUpPackageDto.builder()
                        .code(pkg.getCode())
                        .amount(pkg.getAmount())
                        .points(pkg.getPoints())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public String createPaymentUrl(Long userId, String packageCode, HttpServletRequest request) {
        TopUpPackage topUpPackage = topUpPackageRepository.findByCodeAndIsActiveTrue(packageCode)
                .orElseThrow(() -> new BadRequestException("Invalid or inactive package code"));

        String vnpTxnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 15);

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(topUpPackage.getAmount())
                .points(topUpPackage.getPoints())
                .status(TransactionStatus.PENDING)
                .vnpayTxnRef(vnpTxnRef)
                .build();
        transactionRepository.save(transaction);

        return buildVNPayUrl(topUpPackage.getAmount(), vnpTxnRef, request);
    }

    private String buildVNPayUrl(BigDecimal amount, String vnpTxnRef, HttpServletRequest request) {
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount.longValue() * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", "Top up wallet " + vnpTxnRef);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnpParams.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnpParams.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        String queryUrl = VNPayUtil.buildQueryString(vnpParams);
        String hashData = VNPayUtil.buildHashData(vnpParams);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
        
        return vnPayConfig.getPayUrl() + "?" + queryUrl + "&vnp_SecureHash=" + vnpSecureHash;
    }

    @Override
    @Transactional
    public String processPaymentCallback(Map<String, String> params) {
        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");

        Transaction transaction = transactionRepository.findByVnpayTxnRef(vnpTxnRef)
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        if (!VNPayUtil.verifySignature(params, vnPayConfig.getHashSecret())) {
            updateTransactionStatus(transaction, TransactionStatus.FAILED);
            return vnPayConfig.getReturnUrl() + "?status=failed&message=InvalidSignature";
        }

        if (!TransactionStatus.PENDING.equals(transaction.getStatus())) {
            // Already processed
            return vnPayConfig.getReturnUrl() + "?status=" + transaction.getStatus().name().toLowerCase();
        }

        // Send to Kafka for async processing
        VNPayTransactionEvent event = VNPayTransactionEvent.builder()
                .txnRef(vnpTxnRef)
                .responseCode(vnpResponseCode)
                .build();
        kafkaTemplate.send(KafkaTopicConstants.VNPAY_TRANSACTION_TOPIC, event);

        return vnPayConfig.getReturnUrl() + "?status=processing&txnRef=" + vnpTxnRef;
    }

    @Override
    @Transactional
    public void processTransactionAsync(String txnRef, String responseCode) {
        Transaction transaction = transactionRepository.findByVnpayTxnRef(txnRef)
                .orElseThrow(() -> new BadRequestException("Transaction not found"));

        if (!TransactionStatus.PENDING.equals(transaction.getStatus())) {
            return;
        }

        if ("00".equals(responseCode)) {
            updateTransactionStatus(transaction, TransactionStatus.SUCCESS);
            addPointsToUserWallet(transaction.getUserId(), transaction.getPoints());
        } else {
            updateTransactionStatus(transaction, TransactionStatus.FAILED);
        }
    }

    @Override
    @Transactional
    public void addPointsToUserWallet(Long userId, BigDecimal points) {
        UserWallet userWallet = userWalletRepository.findByUserId(userId)
                .orElseGet(() -> UserWallet.builder()
                        .userId(userId)
                        .points(BigDecimal.ZERO)
                        .build());
        
        userWallet.setPoints(userWallet.getPoints().add(points));
        userWalletRepository.save(userWallet);
    }

    private void updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }
}
