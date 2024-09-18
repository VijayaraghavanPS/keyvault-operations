package com.example.keyvault.service;

import com.example.keyvault.dto.EncryptRequest;
import com.example.keyvault.model.IdRecord;
import com.example.keyvault.repository.IdRecordRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class IdService {

    @Autowired
    private IdRecordRepository repository;

    @Autowired
    private KeyVaultService keyVaultService;

    public Long handlePostRequest(EncryptRequest encryptRequest) {
        String idNumber = encryptRequest.getId();
        String hashValue = hashIdNumber(idNumber);

        Optional<IdRecord> existingRecord = repository.findByHashValue(hashValue);
        if (existingRecord.isPresent()) {
            return existingRecord.get().getId();
        }

        String encryptedValue = keyVaultService.encrypt(idNumber);
        IdRecord record = new IdRecord();
        record.setEncryptedValue(encryptedValue);
        record.setHashValue(hashValue);

        repository.save(record);
        return record.getId();
    }

    public String handleGetRequest(Long id) {
        IdRecord record = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID not found"));

        return keyVaultService.decrypt(record.getEncryptedValue());
    }

    private String hashIdNumber(String idNumber) {
        String salt = "someRandomSalt"; // Securely generate and manage salt in production
        return DigestUtils.sha256Hex(idNumber + salt);
    }
}

