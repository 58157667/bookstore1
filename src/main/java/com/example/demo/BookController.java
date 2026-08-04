package com.example.demo;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequestMapping("/book")
public class BookController {
    @Resource
    private BookSyncService bookSyncService;
  //   同步七牛接口不变
    @GetMapping("/sync")
    public String sync() {
        try {
            bookSyncService.syncQiniuBook();
            return "redirect:/book/list";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    
    
//    @GetMapping("/sync")
//    @ResponseBody // 返回文本，不跳转页面
//    public String sync() {
//        try {
//            bookSyncService.syncQiniuBook();
//            return "同步完成！请刷新图书列表页面查看数据";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "同步失败，异常信息：" + e.getMessage();
//        }
//    }
    // 图书列表：新增分类筛选参数
    @GetMapping("/list")
    public String bookList(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Long categoryId
    ) {
        Map<String, Object> data;
        if (keyword != null && !keyword.trim().isEmpty()) {
            data = bookSyncService.searchBook(keyword, page, categoryId);
        } else {
            data = bookSyncService.getBookList(page, categoryId);
        }
        model.addAllAttributes(data);
        return "bookList";
    }
}