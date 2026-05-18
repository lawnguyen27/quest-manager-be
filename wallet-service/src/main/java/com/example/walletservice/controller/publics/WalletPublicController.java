package com.example.walletservice.controller.publics;

import com.example.walletservice.dto.UserWalletDto;
import com.example.walletservice.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet Public", description = "Public endpoints for users to manage their own wallet")
public class WalletPublicController {

    private final WalletService walletService;

    @GetMapping("/me")
    @Operation(summary = "Get my wallet", description = "Returns the wallet of the currently authenticated user")
    public ResponseEntity<UserWalletDto> getMyWallet() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(principal);
        
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }

    @PostMapping("/me")
    @Operation(summary = "Create my wallet", description = "Create the wallet of the currently authenticated user")
    public ResponseEntity<UserWalletDto> createMyWallet() {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(principal);

        return ResponseEntity.ok(walletService.createWallet(userId));
    }
}
