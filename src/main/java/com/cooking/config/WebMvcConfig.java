package com.cooking.config;

import com.cooking.common.AdminAuthenticationInterceptor;
import com.cooking.common.JwtAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
    
    @Autowired
    private AdminAuthenticationInterceptor adminAuthenticationInterceptor;

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 小程序用户认证拦截器
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/app/**")  // 拦截小程序接口
                .excludePathPatterns(
                        // 用户相关公开接口
                        "/api/app/user/wx-login",                    // 用户登录接口
                        "/api/app/user/login"                        // 用户登录接口（简化版）
                );
        
        // 管理员认证拦截器
        registry.addInterceptor(adminAuthenticationInterceptor)
                .addPathPatterns("/api/admin/**")  // 拦截管理员接口
                .excludePathPatterns(
                        "/api/admin/login",           // 排除管理员登录接口
                        "/api/admin/data-init/**"   , // 排除数据初始化接口（开发环境使用）
                        "/api/admin/file/preview/**"

                );
    }

    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
