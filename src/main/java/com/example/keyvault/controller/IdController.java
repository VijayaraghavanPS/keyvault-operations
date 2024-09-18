package com.example.keyvault.controller;

import com.example.keyvault.dto.EncryptRequest;
import com.example.keyvault.service.IdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IdController {

    @Autowired
    private IdService idService;

    @PostMapping("/store")
    public ResponseEntity<Long> storeId(@RequestBody EncryptRequest encryptRequest) {
        Long id = idService.handlePostRequest(encryptRequest);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/retrieve")
    public ResponseEntity<String> retrieveId(@RequestParam Long id) {
        String decryptedValue = idService.handleGetRequest(id);
        return ResponseEntity.ok(decryptedValue);
    }
}
