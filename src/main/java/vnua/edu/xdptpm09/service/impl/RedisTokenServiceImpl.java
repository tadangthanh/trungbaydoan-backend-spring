//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vnua.edu.xdptpm09.service.impl;

import java.time.Duration;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.service.IRedisService;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements IRedisService {
    private final JedisPool jedisPool;
    private final JwtService jwtService;

    public void saveTokens(String email, String accessToken, String refreshToken) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            long expireTimeAccessToken = this.jwtService.getExpirationTimeToken(accessToken);
            Instant currentTime = Instant.now();
            Instant specificTime = Instant.ofEpochMilli(expireTimeAccessToken);
            int seconds = (int) Duration.between(currentTime, specificTime).getSeconds();
            String key = "email:" + email;
            jedis.hset(key, "accessToken", accessToken);
            jedis.hset(key, "refreshToken", refreshToken);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            throw new BadRequestException(e.getMessage());
        }
    }


    public boolean isAccessTokenValid(String email, String accessToken) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            String key = "email:" + email;
            String storedAccessToken = jedis.hget(key, "accessToken");
            return accessToken.equals(storedAccessToken);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            return false;
        }
    }


    public boolean isRefreshTokenValid(String email, String refreshToken) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            String key = "email:" + email;
            String storedRefreshToken = jedis.hget(key, "refreshToken");
            return refreshToken.equals(storedRefreshToken);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            return false;
        }
    }


    public boolean isBlacklisted(String email, String token) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            String blacklistKey = "blacklist:" + email;
            return jedis.sismember(blacklistKey, token);
        } catch (Exception e) {
            // Log the exception or handle it as needed
            return false;
        }
    }


    public void deleteTokens(String email) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            String key = "email:" + email;
            jedis.del(key);
        } catch (Exception e) {
            // Log the exception or handle it as needed
        }
    }


    public void blacklistToken(String email, String refreshToken) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            long expirationTimeToken = this.jwtService.getExpirationTimeToken(refreshToken);
            Instant currentTime = Instant.now();
            Instant specificTime = Instant.ofEpochMilli(expirationTimeToken);
            int seconds = (int) Duration.between(currentTime, specificTime).getSeconds();

            String blacklistKey = "blacklist:" + email;
            jedis.sadd(blacklistKey, refreshToken);
            jedis.expire(blacklistKey, seconds);
        } catch (Exception e) {
            // Log the exception or handle it as needed
        }
    }


    public void removeTokensBlacklistByEmail(String username) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            String blacklistKey = "blacklist:" + username;
            jedis.del(blacklistKey);
        } catch (Exception e) {
            // Log the exception or handle it as needed
        }
    }


}
