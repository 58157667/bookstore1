package com.example.demo;
import java.time.LocalDate;

import lombok.Data;
@Data
public class Book {
    private Long id;
    private String title;
    private String description;
    private String publisher;
    private LocalDate publicationDate;
    private Long authorId;
    private Long categoryId;
    // 新增：页面展示分类名称，数据库无字段
    private String categoryName;
    // PDF外链 + 七牛文件Key
    private String bookFileUrl;
    private String bookFileUrlPublicId;
    // 封面图外链 + 七牛文件Key
    private String coverImageUrl;
    private String coverImagePublicId;
}