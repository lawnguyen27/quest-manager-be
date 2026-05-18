package com.example.walletservice.controller.privates;

import com.example.common.dto.ApiResponse;
import com.example.walletservice.service.ItemService;
import com.example.walletservice.service.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/private/items")
@RequiredArgsConstructor
public class ItemPrivateController {

    private final ItemService itemService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<ItemDto> createItem(
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(itemService.createItem(itemDto, image));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ApiResponse<ItemDto> updateItem(
            @PathVariable Long id,
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.success(itemService.updateItem(id, itemDto, image));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteItem(@PathVariable Long id) {
        itemService.softDeleteItem(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ItemDto> getItemById(@PathVariable Long id) {
        return ApiResponse.success(itemService.getItemById(id));
    }

    @GetMapping
    public ApiResponse<Page<ItemDto>> getAllItems(Pageable pageable) {
        return ApiResponse.success(itemService.getAllItems(pageable));
    }
}
