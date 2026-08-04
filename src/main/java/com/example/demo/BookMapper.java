package com.example.demo;
//package com.xxx.book.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface BookMapper extends BaseMapper<Book> {
}