package com.example.walletservice.service;

import com.example.walletservice.service.dto.UserItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserItemService {
    UserItemDto buyItem(Long itemId);
    Page<UserItemDto> getMyItems(Pageable pageable);
    Page<UserItemDto> getAllUserItems(Pageable pageable);
    Page<UserItemDto> getUserItemByUserId(Pageable pageable, Long userId);
}
