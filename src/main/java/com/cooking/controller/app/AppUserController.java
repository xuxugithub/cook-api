package com.cooking.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.cooking.common.Result;
import com.cooking.entity.User;
import com.cooking.service.UserService;
import com.cooking.util.JwtUtil;
import com.cooking.util.WeChatUtil;
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

    @Autowired
    private WeChatUtil weChatUtil;

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
            
            // 调用微信API获取openid和session_key
            JSONObject wechatResponse = weChatUtil.getOpenidByCode(code);
            
            // 检查微信API返回结果
            if (wechatResponse.containsKey("errcode")) {
                Integer errcode = wechatResponse.getInteger("errcode");
                String errmsg = wechatResponse.getString("errmsg");
                return Result.error("微信登录失败：" + errmsg + " (错误码：" + errcode + ")");
            }
            
            String openid = wechatResponse.getString("openid");

            if (openid == null || openid.trim().isEmpty()) {
                return Result.error("获取微信openid失败");
            }
            
            String nickName = userInfoMap != null ? (String) userInfoMap.get("nickName") : "微信用户";
            String avatarUrl = userInfoMap != null ? (String) userInfoMap.get("avatarUrl") : "";
            
            // 查找或创建用户
            User user = userService.findOrCreateUser(openid, nickName, avatarUrl);
            
            // 构建JWT载荷
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("userName", user.getNickName());
            claims.put("openid", openid);
            claims.put("type", "user");
            
            // 生成JWT token
            String token = JwtUtil.createJWT(claims);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", user);
            result.put("openid", openid);
            
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
        stats.put("nickName", user.getNickName());
        stats.put("avatarUrl", user.getAvatarUrl());
        
        return Result.success(stats);
    }
}