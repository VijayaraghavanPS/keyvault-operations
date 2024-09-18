package com.example.keyvault.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.keys.cryptography.CryptographyClient;
import com.azure.security.keyvault.keys.cryptography.CryptographyClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureKeyvaultConfiguration {
    @Value("${azure.key-uri}")
    private String keyVaultUri;
    @Value("${azure.key-name}")
    private  String keyName;

//    @Value("${azure.clientid}")
//    private String clientId;
//    @Value("${azure.secret}")
//    private String clientSecret;
//    @Value("${azure.tenantid}")
//    private String tenantId;

    @Bean
    public CryptographyClient cryptographyClient(){
        String keyIdentifier = keyVaultUri + "/keys/" + keyName;
//        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .tenantId(tenantId)
//                .build();
        DefaultAzureCredential defaultAzureCredential = new DefaultAzureCredentialBuilder().build();

        return new CryptographyClientBuilder().keyIdentifier(keyIdentifier).credential(defaultAzureCredential).buildClient();
    }

}
