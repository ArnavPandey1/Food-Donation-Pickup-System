package com.example.relieffeed.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class OTPService {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOTP() {
        int number = secureRandom.nextInt(999999);
        return String.format("%06d", number);
    }
}
