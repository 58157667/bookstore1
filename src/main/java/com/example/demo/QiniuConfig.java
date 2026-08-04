package com.example.demo;
//package com.sync.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;

@Configuration
public class QiniuConfig {

    @Resource
    private QiniuProperties qiniuProperties;

    // 七牛授权Bean
    @Bean
    public Auth qiniuAuth() {
        return Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
    }

    // 文件遍历管理器Bean
    @Bean
    public BucketManager bucketManager(Auth auth) {
        // 全包名引用七牛Configuration，避免和Spring注解冲突
        com.qiniu.storage.Configuration cfg = new com.qiniu.storage.Configuration(Region.huabei());
        return new BucketManager(auth, cfg);
    }
}