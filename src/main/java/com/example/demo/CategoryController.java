package com.example.demo;
import java.util.List;
//import java.util.Locale.Category;

//package com.example.demo;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryDao categoryDao;
    // 获取全部分类（页面下拉使用）
    @GetMapping("/all")
    public String getAll(Model model){
        List<Category> categoryList = categoryDao.listAll();
        model.addAttribute("categoryList",categoryList);
        return "category :: select";
    }
}