package com.cooking.common;

import com.cooking.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 获取token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        // 如果没有token，允许继续访问（公开接口），但不设置userId
        if (token == null || token.isEmpty()) {
            log.debug("请求未携带token，允许访问公开接口: {}", request.getRequestURI());
            return true;
        }

        // 验证token
        if (!JwtUtil.validateToken(token) || JwtUtil.isTokenExpired(token)) {
            log.warn("token无效或已过期: {}", token);
            // token无效，允许继续访问（公开接口），但不设置userId
            return true;
        }

        // 将用户ID放入请求属性，方便后续使用
        try {
            Long userId = JwtUtil.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            log.debug("token验证成功，设置userId: {}", userId);
        } catch (Exception e) {
            log.error("解析token失败", e);
            // token解析失败，允许继续访问（公开接口），但不设置userId
            return true;
        }

        return true;
    }
}
