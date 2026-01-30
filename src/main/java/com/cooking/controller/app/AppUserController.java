package com.cooking.controller.app;

import com.cooking.common.Result;
import com.cooking.entity.User;
import com.cooking.service.UserService;
import com.cooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序用户Controller
 */
@RestController
@RequestMapping("/api/app/user")
public class AppUserController {

    @Autowired
    private UserService userService;

    /**
     * 微信小程序登录
     */
    @PostMapping("/wx-login")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, Object> loginData) {
        try {
            String code = (String) loginData.get("code");
            Map<String, Object> userInfoMap = (Map<String, Object>) loginData.get("userInfo");
            
            if (code == null || code.trim().isEmpty()) {
                return Result.error("微信登录凭证不能为空");
            }
            
            // 模拟通过code获取openid（实际项目中需要调用微信API）
            String openid = "wx_" + code.hashCode();
            
            String nickName = userInfoMap != null ? (String) userInfoMap.get("nickName") : "微信用户";
            String avatarUrl = userInfoMap != null ? (String) userInfoMap.get("avatarUrl") : "";
            
            // 查找或创建用户
            User user = userService.findOrCreateUser(openid, nickName, avatarUrl);
            
            // 构建JWT载荷
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getNickname());
            claims.put("type", "user");
            
            // 生成JWT token
            String token = JwtUtil.createJWT(claims);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", user);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 小程序登录（简化版）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        try {
            String openid = loginData.get("openid");
            String nickName = loginData.get("nickName");
            String avatarUrl = loginData.get("avatarUrl");
            
            if (openid == null || openid.trim().isEmpty()) {
                return Result.error("openid不能为空");
            }
            
            // 查找或创建用户
            User user = userService.findOrCreateUser(openid, nickName, avatarUrl);
            
            // 构建JWT载荷
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("username", user.getNickname());
            claims.put("type", "user");
            
            // 生成JWT token
            String token = JwtUtil.createJWT(claims);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", user);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        User user = userService.getUserWithStats(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        return Result.success(user);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        User user = userService.getUserWithStats(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("fansCount", user.getFansCount() != null ? user.getFansCount() : 0);
        stats.put("followCount", user.getFollowCount() != null ? user.getFollowCount() : 0);
        stats.put("nickname", user.getNickname());
        stats.put("avatar", user.getAvatar());
        
        return Result.success(stats);
    }
}