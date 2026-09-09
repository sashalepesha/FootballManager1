package footballmanager.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameCacheVersionService {

    private final StringRedisTemplate redisTemplate;

    public GameCacheVersionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long getCurrentVersion(String username) {
        String value = redisTemplate.opsForValue().get(versionKeyFor(username));
        return value == null ? 0L : Long.parseLong(value);
    }

    public void incrementVersion(String username) {
        redisTemplate.opsForValue().increment(versionKeyFor(username));
    }

    private String versionKeyFor(String username) {
        return "games:cache-version:" + username;
    }
}
