package com.example.demo;
//package com.sync.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiniuProperties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String domain;
    private String pdfFolder;
    private String coverFolder;
}