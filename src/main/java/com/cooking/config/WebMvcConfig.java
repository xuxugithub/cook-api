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
                .addPathPatterns("/app/**")  // 拦截小程序接口
                .excludePathPatterns(
                        // 用户相关公开接口
                        //"/app/user/wx-login",                    // 用户登录接口
                        //
                        // 基础数据公开接口
                        //"/app/banner/list",                   // Banner列表
                        //"/app/category/list",                 // 分类列表
                        //
                        // 菜品相关公开接口
                        //"/app/dish/page",                     // 菜品分页列表
                        //"/app/dish/*",                        // 菜品详情（数字ID）
                        //"/app/dish/search",                   // 菜品搜索
                        //"/app/dish/hot",                      // 热门菜品
                        //"/app/dish/recommend",                // 推荐菜品
                        //"/app/dish/all",                      // 所有菜品列表
                        //"/app/dish/*/view",                   // 增加浏览量
                        //
                        // 菜品详细信息公开接口
                        //"/app/dish-ingredient/list/*",       // 菜品食材列表
                        //"/app/dish-step/list/*"           // 制作步骤列表
                        //
                        // 用户公开信息接口
                        //"/app/user/profile/**"               // 用户主页相关
                );
        
        // 管理员认证拦截器
        registry.addInterceptor(adminAuthenticationInterceptor)
                .addPathPatterns("/admin/**")  // 拦截管理员接口
                .excludePathPatterns(
                        "/admin/login",           // 排除管理员登录接口
                        "/admin/data-init/**"    // 排除数据初始化接口（开发环境使用）

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
