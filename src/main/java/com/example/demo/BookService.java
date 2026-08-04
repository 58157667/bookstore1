package com.example.demo;
//package com.xxx.book.service;

import com.baomidou.mybatisplus.extension.service.IService;

public interface BookService extends IService<Book> {

    /**
     * 执行七牛云文件遍历、匹配、批量入库
     */
    void syncQiniuBookToDb();
}