package com.example.demo;


import lombok.Data;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    // 新增字段
    private String phone;
    private String email;
    private String remark;
}