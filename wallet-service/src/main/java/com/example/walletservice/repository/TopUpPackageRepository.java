package com.example.walletservice.repository;

import com.example.walletservice.entity.TopUpPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopUpPackageRepository extends JpaRepository<TopUpPackage, Long> {
    Optional<TopUpPackage> findByCodeAndIsActiveTrue(String code);
    List<TopUpPackage> findAllByIsActiveTrue();
}
