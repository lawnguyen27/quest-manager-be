package com.example.walletservice.controller.publics;

import com.example.walletservice.dto.TopUpRequestDto;
import com.example.walletservice.service.TopUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/public/top-up")
@RequiredArgsConstructor
@Tag(name = "Top Up Public", description = "Public endpoints for wallet top-up and VNPay integration")
public class TopUpPublicController {

    private final TopUpService topUpService;

    @GetMapping("/packages")
    @Operation(summary = "Get available top-up packages")
    public ResponseEntity<?> getAvailablePackages() {
        return ResponseEntity.ok(topUpService.getAvailablePackages());
    }

    @PostMapping("/create")
    @Operation(summary = "Create top-up payment URL")
    public ResponseEntity<?> createPaymentUrl(
            @RequestBody TopUpRequestDto requestDto,
            HttpServletRequest request) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(principal);
        String url = topUpService.createPaymentUrl(userId, requestDto.getPackageCode(), request);
        return ResponseEntity.ok(Collections.singletonMap("paymentUrl", url));
    }
}
