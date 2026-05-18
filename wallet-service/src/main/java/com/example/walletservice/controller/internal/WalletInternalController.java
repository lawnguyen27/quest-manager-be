package com.example.walletservice.controller.internal;

import com.example.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/internal/wallet")
@RequiredArgsConstructor
public class WalletInternalController {

    private final WalletService walletService;

    @PostMapping("/points/add")
    public ResponseEntity<Void> addPoints(@RequestParam Long userId, @RequestParam BigDecimal points) {
        walletService.addPoints(userId, points);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/points/deduct")
    public ResponseEntity<Void> deductPoints(@RequestParam Long userId, @RequestParam BigDecimal points) {
        walletService.deductPoints(userId, points);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@RequestParam Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }
}
