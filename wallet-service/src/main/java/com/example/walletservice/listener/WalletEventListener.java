package com.example.walletservice.listener;

import com.example.common.constants.KafkaTopicConstants;
import com.example.common.event.PointAwardEvent;
import com.example.walletservice.event.VNPayTransactionEvent;
import com.example.walletservice.service.TopUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {

    private final TopUpService topUpService;

    @KafkaListener(topicPattern = KafkaTopicConstants.ADD_POINTS_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "listenerFactory")
    public void handlePointAward(PointAwardEvent event) {
        log.info("Received PointAwardEvent: {}", event);
        try {
            topUpService.addPointsToUserWallet(event.getUserId(), event.getPoints());
            log.info("Successfully added points for userId: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to add points for userId: {}. Error: {}", event.getUserId(), e.getMessage());
        }
    }

    @KafkaListener(topicPattern = KafkaTopicConstants.VNPAY_TRANSACTION_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "listenerFactory")
    public void handleVNPayTransaction(VNPayTransactionEvent event) {
        log.info("Received VNPayTransactionEvent: {}", event);
        try {
            topUpService.processTransactionAsync(event.getTxnRef(), event.getResponseCode());
            log.info("Successfully processed VNPay transaction asynchronously for txnRef: {}", event.getTxnRef());
        } catch (Exception e) {
            log.error("Failed to process VNPay transaction for txnRef: {}. Error: {}", event.getTxnRef(), e.getMessage());
        }
    }
}
