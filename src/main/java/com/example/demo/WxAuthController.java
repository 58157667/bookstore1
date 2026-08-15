package com.example.demo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx")
public class WxAuthController {
    @Resource private UserDao userDao;
    @Resource private PasswordEncoder passwordEncoder;
    @Resource private WxTokenStore tokenStore;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        Map<String, Object> r = new HashMap<>();
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            r.put("success", false); r.put("message", "请输入用户名和密码"); return r;
        }
        SysUser user = userDao.findByUsername(username.trim());
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            r.put("success", false); r.put("message", "用户名或密码错误"); return r;
        }
        r.put("success", true);
        r.put("username", user.getUsername());
        r.put("token", tokenStore.create(user.getUsername()));
        return r;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value="Authorization", required=false) String authorization) {
        String token = token(authorization); tokenStore.remove(token);
        Map<String, Object> r = new HashMap<>(); r.put("success", true); return r;
    }
    private String token(String h) { return h != null && h.startsWith("Bearer ") ? h.substring(7) : null; }
}
