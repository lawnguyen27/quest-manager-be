package com.example.walletservice.service;

import com.example.walletservice.service.dto.ItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, MultipartFile image);

    ItemDto updateItem(Long id, ItemDto itemDto, MultipartFile image);
    void softDeleteItem(Long id);
    ItemDto getItemById(Long id);
    Page<ItemDto> getAllItems(Pageable pageable);
    Page<ItemDto> getAvailableItems(Pageable pageable);
}
