package com.lee.walletwise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${otp.expiration}")
    private long otpExpiration;
    
    @Value("${otp.length}")
    private int otpLength;
    
    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public String generateAndStoreOtp(String identifier) {
        String otp = generateOtp();
        String key = OTP_PREFIX + identifier;
        
        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(key, otp, otpExpiration, TimeUnit.SECONDS);
        
        log.info("OTP generated and stored for identifier: {} (expires in {} seconds)", identifier, otpExpiration);
        
        return otp;
    }
    
    public boolean verifyOtp(String identifier, String providedOtp) {
        String key = OTP_PREFIX + identifier;
        String storedOtp = (String) redisTemplate.opsForValue().get(key);
        
        if (storedOtp == null) {
            log.warn("OTP not found or expired for identifier: {}", identifier);
            return false;
        }
        
        boolean isValid = storedOtp.equals(providedOtp);
        
        if (isValid) {
            // Delete OTP after successful verification
            redisTemplate.delete(key);
            log.info("OTP verified successfully for identifier: {}", identifier);
        } else {
            log.warn("Invalid OTP provided for identifier: {}", identifier);
        }
        
        return isValid;
    }
    
    public void deleteOtp(String identifier) {
        String key = OTP_PREFIX + identifier;
        redisTemplate.delete(key);
        log.info("OTP deleted for identifier: {}", identifier);
    }
    
    public boolean isOtpExists(String identifier) {
        String key = OTP_PREFIX + identifier;
        return redisTemplate.hasKey(key);
    }
    
    public long getOtpTtl(String identifier) {
        String key = OTP_PREFIX + identifier;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    
    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
}