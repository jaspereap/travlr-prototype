package nus.iss.travlr.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    @Value("${session.timeout}")
    private Integer SESSION_TIMEOUT;

    @Value("${session.key}")
    private String SESSION_KEY;

    public void loginUser(String key, String value) {
        System.out.println("\tNew Login, Adding user session");
        template.opsForHash().put(SESSION_KEY, key, value);
    }

    public String getCurrentUserId(String sessId) {
        return (String) template.opsForHash().get(SESSION_KEY, sessId);
    }

    public void logoutUser(String sessId) {
        template.opsForHash().delete(SESSION_KEY, sessId);
    }
}
