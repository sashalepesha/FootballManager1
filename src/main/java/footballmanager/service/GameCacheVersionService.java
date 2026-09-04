package footballmanager.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameCacheVersionService {

    private final StringRedisTemplate redisTemplate;
    private static final String VERSION_KEY = "games:cache-version";

    public GameCacheVersionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long getCurrentVersion() {
        String value = redisTemplate.opsForValue().get(VERSION_KEY);
        return value == null ? 0L : Long.parseLong(value);
    }

    public void incrementVersion() {
        redisTemplate.opsForValue().increment(VERSION_KEY);
    }
}
