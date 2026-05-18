package com.example.walletservice.controller.publics;

import com.example.common.dto.ApiResponse;
import com.example.walletservice.service.ItemService;
import com.example.walletservice.service.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/items")
@RequiredArgsConstructor
public class ItemPublicController {

    private final ItemService itemService;

    @GetMapping
    public ApiResponse<Page<ItemDto>> getAvailableItems(Pageable pageable) {
        return ApiResponse.success(itemService.getAvailableItems(pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ItemDto> getItemById(@PathVariable Long id) {
        return ApiResponse.success(itemService.getItemById(id));
    }
}
