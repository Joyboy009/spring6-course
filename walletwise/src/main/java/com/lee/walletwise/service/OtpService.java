package com.lee.walletwise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final String OTP_KEY_PREFIX = "OTP_";

    private final StringRedisTemplate redisTemplate;
    private final Random random = new Random();

    public String generateAndStoreOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        redisTemplate.opsForValue().set(OTP_KEY_PREFIX + email, otp, OTP_TTL);
        // In a real system send via email/SMS here
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        String key = OTP_KEY_PREFIX + email;
        String stored = redisTemplate.opsForValue().get(key);
        if (stored != null && stored.equals(otp)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
}