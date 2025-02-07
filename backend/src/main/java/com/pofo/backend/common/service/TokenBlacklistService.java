package com.pofo.backend.common.service;

/*
* Redis 블랙 리스트 관리
* */

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    /**
     * AccessToken을 블랙리스트에 추가 (로그아웃 시)
     */
    public void addToBlacklist(String token, long expirationTimeMillis) {
        long expirationSeconds = expirationTimeMillis / 1000; // 밀리초 → 초 변환
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "logout", Duration.ofSeconds(expirationSeconds));
    }

    /**
     * AccessToken이 블랙리스트에 포함되어 있는지 확인
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
