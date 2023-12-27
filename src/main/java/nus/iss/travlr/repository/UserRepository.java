package nus.iss.travlr.repository;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import nus.iss.travlr.model.User;

@Repository
public class UserRepository {
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    @Value("${user.key}")
    private String key;

    public boolean hasUser(String hashKey) {
        return template.opsForHash().hasKey(key, hashKey);
    }

    public User getUser(String hashKey) {
        String userString = (String) template.opsForHash().get(key, hashKey);
        JsonReader jreader = Json.createReader(new StringReader(userString));
        JsonObject userJObject = jreader.readObject();
        String userId = userJObject.getString("userId", "NULL");
        String userName = userJObject.getString("userName", "NULL");
        String password = userJObject.getString("password", "NULL");
        User output = new User(userId, userName, password);
        return output;
    }

    public void addUser(User user) {
        template.opsForHash().put(key, user.getUserName(), user.toJsonObject().toString());
    }

    public boolean delUser(String hashKey) {
        if (template.opsForHash().delete(key, hashKey) != null) {
            return true;
        }
        return false;
    }
    
}
