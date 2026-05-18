package com.example.common.api.user;

import com.example.common.config.feign.BaseFeignClientRequestIntercepter;
import com.example.common.dto.NotificationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${exchange.services.user-service.url:http://localhost:8081}", configuration = BaseFeignClientRequestIntercepter.class)
public interface UserClient {

    @GetMapping(value = "/internal/users/validate", produces = "application/json")
    ResponseEntity<Boolean> validateUser(@RequestParam("userId") Long userId);

    @PostMapping(value = "/internal/notifications/push", consumes = "application/json")
    ResponseEntity<Void> pushNotification(NotificationRequestDto request);
}
