package com.example.walletservice.service.impl;

import com.example.common.config.exception.BadRequestException;
import com.example.common.config.exception.NotFoundException;
import com.example.walletservice.entity.Item;
import com.example.walletservice.entity.UserItem;
import com.example.walletservice.entity.UserWallet;
import com.example.walletservice.repository.ItemRepository;
import com.example.walletservice.repository.UserItemRepository;
import com.example.walletservice.repository.UserWalletRepository;
import com.example.walletservice.service.UserItemService;
import com.example.walletservice.service.dto.UserItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserItemServiceImpl implements UserItemService {

    private final UserItemRepository userItemRepository;
    private final ItemRepository itemRepository;
    private final UserWalletRepository userWalletRepository;

    @Override
    @Transactional
    public UserItemDto buyItem(Long itemId) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(userIdStr);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!Boolean.TRUE.equals(item.getIsAvailable())) {
            throw new BadRequestException("Item is not available for purchase");
        }

        UserWallet wallet = userWalletRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User wallet not found"));

        if (wallet.getPoints().compareTo(item.getPrice()) < 0) {
            throw new BadRequestException("Not enough points to buy this item");
        }

        wallet.setPoints(wallet.getPoints().subtract(item.getPrice()));
        userWalletRepository.save(wallet);

        UserItem userItem = UserItem.builder()
                .userId(userId)
                .itemId(item.getId())
                .purchaseDate(Instant.now())
                .price(item.getPrice())
                .build();

        return mapToDto(userItemRepository.save(userItem), item.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserItemDto> getMyItems(Pageable pageable) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(userIdStr);
        return userItemRepository.findByUserId(userId, pageable)
                .map(ui -> {
                    String itemName = itemRepository.findById(ui.getItemId())
                            .map(Item::getName)
                            .orElse("Unknown Item");
                    return mapToDto(ui, itemName);
                });
    }

    @Override
    public Page<UserItemDto> getAllUserItems(Pageable pageable) {
        return userItemRepository.findAll(pageable).map(ui -> {
            String itemName = itemRepository.findById(ui.getItemId())
                    .map(Item::getName)
                    .orElse("Unknown Item");
            return mapToDto(ui, itemName);
        });
    }

    @Override
    public Page<UserItemDto> getUserItemByUserId(Pageable pageable, Long userId) {
        return userItemRepository.findByUserId(userId, pageable)
                .map(ui -> {
                    String itemName = itemRepository.findById(ui.getItemId())
                            .map(Item::getName)
                            .orElse("Unknown Item");
                    return mapToDto(ui, itemName);
                });
    }

    private UserItemDto mapToDto(UserItem userItem, String itemName) {
        return UserItemDto.builder()
                .id(userItem.getId())
                .userId(userItem.getUserId())
                .itemId(userItem.getItemId())
                .itemName(itemName)
                .purchaseDate(userItem.getPurchaseDate())
                .price(userItem.getPrice())
                .build();
    }
}
