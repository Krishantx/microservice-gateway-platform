package com.github.krishantx.RLaaS.Service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {
    
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateApiKey() {
        byte[] randomByte = new byte[32]; //256 bits
        secureRandom.nextBytes(randomByte);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomByte);
    }
}
