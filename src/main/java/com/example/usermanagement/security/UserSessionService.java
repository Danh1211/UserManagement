package com.example.usermanagement.security;

import com.example.usermanagement.config.UserSessionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionService {

    private final JwtProperties jwtProperties;
    private final RedissonClient redisson;
    private final ObjectMapper objectMapper;

    private String getKeyUserSession(String username){
        return "user_session:" + username;
    }

    private String getKeyToken(String username){
        return "token:" + username;
    }

    private RBucket<String> getBucket(String key) {return redisson.getBucket(key, StringCodec.INSTANCE);}

    public void cacheToken(String username, String token){
        try {
            Duration ttl = Duration.ofSeconds(jwtProperties.getTokenValidityInSeconds());
            String key = this.getKeyToken(username);
            this.getBucket(key).set((hashToken(token)), ttl);
        } catch (Exception e) {
            log.error("cacheToken error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void cacheSession(String username, UserSessionInfo userSession) {
        try {
            String key = this.getKeyUserSession(username);
            String data = objectMapper.writeValueAsString(userSession);
            Duration ttl = Duration.of(jwtProperties.getTokenValidityInSeconds() + 5 * 60,
                    ChronoUnit.SECONDS);
            this.getBucket(key).set(data, ttl);
        }catch (Exception e) {
            log.error("initCacheSession error: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<UserSessionInfo> getUserSession(String username) {
        String data = this.getBucket(this.getKeyUserSession(username)).get();
        if (!StringUtils.hasText(data)) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(data, UserSessionInfo.class));
        } catch (Exception e) {
            log.error("Cannot parse session of {}", username, e);
            return Optional.empty();
        }
    }

    public boolean isValidToken(String username, String token){
        try {
            String key = this.getKeyToken(username);
            String value = this.getBucket(key).get();
            return StringUtils.hasText(value) && hashToken(token).equals(value);
        }catch (Exception e){
            log.error("Validate user token error: {}", e.getMessage());
            return false;
        }
    }

    public void revoke(String username) {
        redisson.getKeys().delete(getKeyToken(username), getKeyUserSession(username));
    }

    public boolean isUserOnline(String username) {
        try {
            return this.getBucket(this.getKeyToken(username)).isExists();
        } catch (Exception e) {
            log.error("Check online error: {}", e.getMessage());
            return false;
        }
    }

    private String hashToken(String content){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);
        }catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
