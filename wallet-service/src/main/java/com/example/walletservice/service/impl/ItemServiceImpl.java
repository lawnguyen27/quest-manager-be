package com.example.walletservice.service.impl;

import com.example.common.config.exception.NotFoundException;
import com.example.common.service.FileUploadService;
import com.example.walletservice.entity.Item;
import com.example.walletservice.repository.ItemRepository;
import com.example.walletservice.service.ItemService;
import com.example.walletservice.service.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, MultipartFile image) {
        String imageLink = null;
        if (image != null && !image.isEmpty()) {
            imageLink = fileUploadService.uploadFile(image);
        } else if (StringUtils.hasText(itemDto.getImageLink())) {
            imageLink = itemDto.getImageLink();
        }
        boolean available = itemDto.getIsAvailable() == null || Boolean.TRUE.equals(itemDto.getIsAvailable());
        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .price(itemDto.getPrice())
                .imageLink(imageLink)
                .isAvailable(available)
                .build();
        return mapToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long id, ItemDto itemDto, MultipartFile image) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setPrice(itemDto.getPrice());
        if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        }
        if (image != null && !image.isEmpty()) {
            item.setImageLink(fileUploadService.uploadFile(image));
        } else if (StringUtils.hasText(itemDto.getImageLink())) {
            item.setImageLink(itemDto.getImageLink());
        }
        return mapToDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void softDeleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        item.setIsAvailable(false);
        itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id) {
        return itemRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDto> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDto> getAvailableItems(Pageable pageable) {
        return itemRepository.findByIsAvailableTrue(pageable).map(this::mapToDto);
    }

    private ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageLink(item.getImageLink())
                .isAvailable(Boolean.TRUE.equals(item.getIsAvailable()))
                .build();
    }
}
