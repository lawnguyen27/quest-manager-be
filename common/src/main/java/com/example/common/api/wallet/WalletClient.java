package com.example.common.api.wallet;

import com.example.common.config.feign.BaseFeignClientRequestIntercepter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "wallet-service", url = "${exchange.services.wallet-service.url:http://localhost:8083}", configuration = BaseFeignClientRequestIntercepter.class)
public interface WalletClient {

    @PostMapping(value = "/internal/wallet/points/add", produces = "application/json")
    ResponseEntity<Void> addPoints(@RequestParam("userId") Long userId, @RequestParam("points") BigDecimal points);

    @PostMapping(value = "/internal/wallet/points/deduct", produces = "application/json")
    ResponseEntity<Void> deductPoints(@RequestParam("userId") Long userId, @RequestParam("points") BigDecimal points);

    @GetMapping(value = "/internal/wallet/balance", produces = "application/json")
    ResponseEntity<BigDecimal> getBalance(@RequestParam("userId") Long userId);
}
