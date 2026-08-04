package com.example.demo;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class BookDao {
    @Resource
    private JdbcTemplate jdbcTemplate;
    // ========== 原有插入、判重方法保留 ==========
    public int insertBook(Book book) {
        String sql = "INSERT INTO book (" +
                "title, description, publisher, publication_date, author_id, category_id, " +
                "book_file_url, book_file_url_public_id, cover_image_url, cover_image_public_id" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,
                book.getTitle(),
                book.getDescription(),
                book.getPublisher(),
                book.getPublicationDate(),
                book.getAuthorId(),
                book.getCategoryId(),
                book.getBookFileUrl(),
                book.getBookFileUrlPublicId(),
                book.getCoverImageUrl(),
                book.getCoverImagePublicId()
        );
    }
    public boolean existsByTitle(String title) {
        String countSql = "SELECT COUNT(1) FROM book WHERE title = ?";
        Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, title);
        return count != null && count > 0;
    }

    // ========== 分页查询 关联分类名称 ==========
    public List<Book> listAllBook(int pageNum, int pageSize, Long categoryId) {
    	StringBuilder sql = new StringBuilder("SELECT b.*, c.name as category_name FROM book b LEFT JOIN category c ON b.category_id = c.id ");
        Object[] params = new Object[0];
        if(categoryId != null){
            sql.append(" WHERE b.category_id = ? ");
            params = new Object[]{categoryId, pageSize, (pageNum-1)*pageSize};
        }else{
            params = new Object[]{pageSize, (pageNum-1)*pageSize};
        }
        sql.append(" ORDER BY b.id DESC LIMIT ? OFFSET ?");
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Book.class), params);
    }

    // ========== 按书名+分类模糊搜索 ==========
    public List<Book> searchBookByTitle(String keyword, Long categoryId, int pageNum, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT b.*, c.name as category_name FROM book b LEFT JOIN category c ON b.category_id = c.id WHERE title ILIKE ? ");
        int paramIdx = 1;
        Object[] params;
        if(categoryId != null){
            sql.append(" AND b.category_id = ? ");
            params = new Object[]{"%"+keyword+"%", categoryId, pageSize, (pageNum-1)*pageSize};
        }else{
            params = new Object[]{"%"+keyword+"%", pageSize, (pageNum-1)*pageSize};
        }
        sql.append(" ORDER BY b.id DESC LIMIT ? OFFSET ?");
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Book.class), params);
    }

    // 总条数-全部/按分类
    public int countAll(Long categoryId) {
        String sql;
        Object[] param;
        if(categoryId != null){
            sql = "SELECT COUNT(1) FROM book WHERE category_id = ?";
            param = new Object[]{categoryId};
        }else{
            sql = "SELECT COUNT(1) FROM book";
            param = new Object[]{};
        }
        return jdbcTemplate.queryForObject(sql, Integer.class, param);
    }
    // 搜索总条数【修复：sql.toString()】
    public int countSearch(String keyword, Long categoryId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM book WHERE title ILIKE ? ");
        Object[] params;
        if(categoryId != null){
            sql.append(" AND category_id = ?");
            params = new Object[]{"%"+keyword+"%", categoryId};
        }else{
            params = new Object[]{"%"+keyword+"%"};
        }
        // 关键修复：调用toString()转为字符串
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params);
    }
}
