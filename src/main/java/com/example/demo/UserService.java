package com.example.demo;

import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private PasswordEncoder passwordEncoder;

    // 手机号正则简单校验
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 带手机号、邮箱、备注的注册逻辑
     */
    public String register(String username, String password, String phone, String email, String remark) {
        // 1. 用户名校验
        if (!StringUtils.hasText(username) || username.length() < 2) {
            return "用户名至少2个字符";
        }
        // 2. 密码校验
        if (!StringUtils.hasText(password) || password.length() < 4) {
            return "密码至少4个字符";
        }
        // 3. 手机号 必填+格式校验
        if (!StringUtils.hasText(phone)) {
            return "手机号码为必填项";
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "手机号码格式不正确";
        }
        // 4. 用户名重复判断
        if (userDao.existUsername(username)) {
            return "该用户名已被注册";
        }

        // 密码加密
        String encryptPwd = passwordEncoder.encode(password);
        userDao.registerUser(username, encryptPwd, phone, email, remark);
        return "success";
    }
}