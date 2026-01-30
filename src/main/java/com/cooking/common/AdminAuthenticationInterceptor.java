package com.cooking.common;

import com.cooking.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员JWT认证拦截器
 */
@Slf4j
@Component
public class AdminAuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("token");
        
        if (token == null || token.trim().isEmpty()) {
            log.warn("管理员接口访问失败：token为空");
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
            return false;
        }

        try {
            // 验证token有效性
            if (!JwtUtil.validateToken(token)) {
                log.warn("管理员接口访问失败：token无效");
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"token无效\",\"data\":null}");
                return false;
            }

            // 验证是否为管理员token
            if (!JwtUtil.isAdminToken(token)) {
                log.warn("管理员接口访问失败：非管理员token");
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":403,\"message\":\"权限不足\",\"data\":null}");
                return false;
            }

            // 获取管理员ID并设置到请求属性中
            Long adminId = JwtUtil.getAdminIdFromToken(token);
            request.setAttribute("adminId", adminId);
            
            return true;
        } catch (Exception e) {
            log.error("管理员token验证异常：", e);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token验证失败\",\"data\":null}");
            return false;
        }
    }
}