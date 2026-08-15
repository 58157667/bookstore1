package com.example.demo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

@Component
public class WxTokenStore {
    private final ConcurrentMap<String, String> tokens = new ConcurrentHashMap<>();
    public String create(String username) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokens.put(token, username);
        return token;
    }
    public boolean contains(String token) { return token != null && tokens.containsKey(token); }
    public void remove(String token) { if (token != null) tokens.remove(token); }
}
