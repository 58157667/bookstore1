package com.example.demo;
import java.util.List;
//import java.util.Locale.Category;

//package com.example.demo;
import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class CategoryDao {
    @Resource
    private JdbcTemplate jdbcTemplate;
    // 查询所有分类
    public List<Category> listAll(){
        String sql = "SELECT * FROM category ORDER BY id";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Category.class));
    }
    // 根据id查询分类
    public Category getById(Long id){
        String sql = "SELECT * FROM category WHERE id = ?";
        try{
            return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(Category.class),id);
        }catch (Exception e){
            return null;
        }
    }
}