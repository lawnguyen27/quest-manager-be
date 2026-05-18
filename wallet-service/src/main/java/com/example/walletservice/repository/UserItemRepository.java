package com.example.walletservice.repository;

import com.example.walletservice.entity.UserItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    Page<UserItem> findByUserId(Long userId, Pageable pageable);
}
