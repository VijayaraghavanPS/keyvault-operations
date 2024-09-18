package com.example.keyvault.repository;

import com.example.keyvault.model.IdRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdRecordRepository extends JpaRepository<IdRecord, Long> {
    Optional<IdRecord> findByHashValue(String hashValue);
}
