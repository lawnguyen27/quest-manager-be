package com.example.walletservice.controller.privates;

import com.example.common.dto.ApiResponse;
import com.example.walletservice.service.UserItemService;
import com.example.walletservice.service.dto.UserItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/user-items")
@RequiredArgsConstructor
public class UserItemPrivateController {

    private final UserItemService userItemService;

    @GetMapping()
    public ApiResponse<Page<UserItemDto>> getAllUserItem(Pageable pageable) {
        return ApiResponse.success(userItemService.getAllUserItems(pageable));
    }

    @GetMapping("/{userId}")
    public ApiResponse<Page<UserItemDto>> getUserItemByUserId(Pageable pageable, @PathVariable Long userId) {
        return ApiResponse.success(userItemService.getUserItemByUserId(pageable, userId));
    }
}
