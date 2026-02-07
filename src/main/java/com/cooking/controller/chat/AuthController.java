package com.cooking.controller.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cooking.common.Result;
import com.cooking.dto.UserLoginRequest;
import com.cooking.dto.UserRegisterRequest;
import com.cooking.entity.User;
import com.cooking.service.UserService;
import com.cooking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody UserRegisterRequest request, 
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        
        try {
            // 检查手机号是否已注册
            User existUser = userService.findByPhone(request.getPhone());
            if (existUser != null) {
                return Result.error("该手机号已注册");
            }
            
            // 检查邮箱是否已注册
            existUser = userService.findByEmail(request.getEmail());
            if (existUser != null) {
                return Result.error("该邮箱已注册");
            }
            
            // 创建用户
            User user = new User();
            user.setNickName(request.getName());
            user.setOpenid("chat_"+ UUID.randomUUID());
            user.setPhone(request.getPhone());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            
            userService.save(user);
            
            // 生成token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("userName", user.getNickName());
            claims.put("type", "user");
            
            String token = JwtUtil.createJWT(claims);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", user);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginRequest request,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        
        try {
            // 查找用户
            User user = userService.findByPhone(request.getPhone());
            if (user == null) {
                return Result.error("用户不存在，请先注册");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return Result.error("密码错误");
            }
            
            // 生成token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("userName", user.getNickName());
            claims.put("type", "user");
            
            String token = JwtUtil.createJWT(claims);
            
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", user);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }
}
