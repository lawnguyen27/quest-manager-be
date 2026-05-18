package com.example.walletservice.controller.common;

import com.example.walletservice.service.TopUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/common/webhook")
@RequiredArgsConstructor
@Tag(name = "Top Up Webhook", description = "Common unauthenticated endpoints for webhooks")
public class TopUpWebhookController {

    private final TopUpService topUpService;

    @GetMapping("/vnpay-return")
    @Operation(summary = "VNPay return URL")
    public ResponseEntity<?> vnpayReturn(
            @RequestParam Map<String, String> allParams,
            HttpServletResponse response) throws IOException {
        String redirectUrl = topUpService.processPaymentCallback(allParams);
        response.sendRedirect(redirectUrl);
        return ResponseEntity.ok().build();
    }
}
