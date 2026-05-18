package com.example.walletservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.walletservice.service.UserItemService;
import com.example.walletservice.service.dto.UserItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user-items")
@RequiredArgsConstructor
public class UserItemPublicController {

    private final UserItemService userItemService;

    @PostMapping("/buy/{itemId}")
    public ApiResponse<UserItemDto> buyItem(@PathVariable Long itemId) {
        return ApiResponse.success(userItemService.buyItem(itemId));
    }

    @GetMapping("/my")
    public ApiResponse<Page<UserItemDto>> getMyItems(Pageable pageable) {
        return ApiResponse.success(userItemService.getMyItems(pageable));
    }
}
