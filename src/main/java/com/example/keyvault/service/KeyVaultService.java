package com.example.keyvault.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.keys.KeyClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClientBuilder;
import com.azure.security.keyvault.keys.cryptography.models.EncryptResult;
import com.azure.security.keyvault.keys.cryptography.models.DecryptResult;
import com.azure.security.keyvault.keys.cryptography.models.EncryptionAlgorithm;
import com.azure.security.keyvault.keys.models.CreateKeyOptions;
import com.azure.security.keyvault.keys.models.KeyType;
import com.azure.security.keyvault.keys.models.KeyVaultKey;
import com.azure.security.keyvault.keys.KeyClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KeyVaultService {

    private final CryptographyClient cryptographyClient;

    public String encrypt(String value) {
        try{
            // Convert the value to bytes
            byte[] data = value.getBytes(StandardCharsets.UTF_8);

            // Encrypt data using RSA-OAEP algorithm
            EncryptResult encryptResult = cryptographyClient.encrypt(EncryptionAlgorithm.RSA_OAEP, data);

            // Encode encrypted bytes to Base64
            return Base64.getEncoder().encodeToString(encryptResult.getCipherText());
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException(e);
        }


    }

    public String decrypt(String encryptedValue) {
        // Decode Base64 encrypted value to bytes
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue);

        // Decrypt the data using RSA-OAEP algorithm
        DecryptResult decryptResult = cryptographyClient.decrypt(EncryptionAlgorithm.RSA_OAEP, encryptedBytes);

        // Convert decrypted bytes to String
        return new String(decryptResult.getPlainText(), StandardCharsets.UTF_8);
    }
}


