package com.example.demo;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    // 根据用户名查询用户（Security登录）
    public SysUser findByUsername(String username) {
        String sql = "SELECT * FROM sys_user WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SysUser.class), username);
        } catch (Exception e) {
            return null;
        }
    }

    // 【修改】带手机号、邮箱、备注注册入库
    public int registerUser(String username, String encryptPwd, String phone, String email, String remark) {
        String sql = "INSERT INTO sys_user(username, password, phone, email, remark) VALUES (?,?,?,?,?)";
        return jdbcTemplate.update(sql, username, encryptPwd, phone, email, remark);
    }

    // 判断用户名是否存在
    public boolean existUsername(String username) {
        String sql = "SELECT count(1) FROM sys_user WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}