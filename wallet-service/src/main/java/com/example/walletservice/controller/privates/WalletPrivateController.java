package com.example.walletservice.controller.privates;

import com.example.walletservice.dto.UserWalletDto;
import com.example.walletservice.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/private/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet Private", description = "Admin endpoints for wallet management")
public class WalletPrivateController {

    private final WalletService walletService;

    @GetMapping("/all")
    @Operation(summary = "Get all wallets", description = "Returns a list of all user wallets")
    public ResponseEntity<List<UserWalletDto>> getAllWallets() {
        return ResponseEntity.ok(walletService.getAllWallets());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get wallet by userId", description = "Returns the wallet of a specific user")
    public ResponseEntity<UserWalletDto> getWalletByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }
}
