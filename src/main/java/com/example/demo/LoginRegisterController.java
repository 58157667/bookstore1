package com.example.demo;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginRegisterController {

    @Resource
    private UserService userService;

    // 登录页
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 注册页
    @GetMapping("/registerwws")
    public String registerPage() {
        return "registerwws";
    }

    // 【修改】接收手机号、邮箱、备注
    @PostMapping("/doRegister")
    public String doRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String remark,
            Model model
    ) {
        String result = userService.register(username, password, phone, email, remark);
        if ("success".equals(result)) {
            return "redirect:/login?reg=ok";
        }
        model.addAttribute("msg", result);
        // 回显填写内容
        model.addAttribute("username", username);
        model.addAttribute("phone", phone);
        model.addAttribute("email", email);
        model.addAttribute("remark", remark);
        return "registerwws";
    }
}
