package com.example.demo;

import java.util.*;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wx")
public class WxBookController {
    @Resource private BookSyncService bookSyncService;
    @Resource private CategoryDao categoryDao;
    @Resource private WxTokenStore tokenStore;

    @GetMapping("/books")
    public Map<String, Object> books(@RequestHeader(value="Authorization", required=false) String authorization,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Integer page,
            @RequestParam(required=false) Long categoryId) {
        if (!authorized(authorization)) return unauthorized();
        Map<String, Object> data = keyword != null && !keyword.trim().isEmpty()
                ? bookSyncService.searchBook(keyword.trim(), page, categoryId)
                : bookSyncService.getBookList(page, categoryId);
        data.put("success", true);
        return data;
    }

    @GetMapping("/categories")
    public Object categories(@RequestHeader(value="Authorization", required=false) String authorization) {
        if (!authorized(authorization)) return unauthorized();
        return categoryDao.listAll();
    }

    private boolean authorized(String h) {
        return h != null && h.startsWith("Bearer ") && tokenStore.contains(h.substring(7));
    }
    private Map<String,Object> unauthorized() {
        Map<String,Object> r=new HashMap<>(); r.put("success",false); r.put("message","请先登录"); r.put("list", Collections.emptyList()); return r;
    }
}
