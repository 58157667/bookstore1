package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@MapperScan("com.example.demo")
@EnableConfigurationProperties
public class EbookUpload3Application {
    public static void main(String[] args) {
        SpringApplication.run(EbookUpload3Application.class, args);
    }
}