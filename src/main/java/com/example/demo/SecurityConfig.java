package com.example.demo;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private UserDao userDao;

    // 密码加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 数据库查询用户
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            SysUser user = userDao.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("用户名不存在");
            }
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // 仅放行 登录页、注册相关，删掉了 /** 全部放行
                .antMatchers("/login", "/register", "/doRegister").permitAll()
                // 所有book接口必须登录：/book/list /book/sync 都受保护
                .antMatchers("/book/**").authenticated()
                // 其余所有请求都需要认证
                .anyRequest().authenticated()

                .and()
                // 表单登录配置：未登录拦截自动跳转 loginPage="/login"
                .formLogin()
                .loginPage("/login")               // 拦截后跳转的地址
                .loginProcessingUrl("/doLogin")    // 登录提交地址，Security内部处理
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/book/list", true) // 登录成功强制进图书列表
                .permitAll()

                .and()
                // 退出登录
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()

                .and()
                .csrf().disable();

        return http.build();
    }
}