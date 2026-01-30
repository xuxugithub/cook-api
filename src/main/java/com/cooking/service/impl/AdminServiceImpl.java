package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.dto.AdminLoginDTO;
import com.cooking.dto.AdminLoginResultDTO;
import com.cooking.entity.Admin;
import com.cooking.mapper.AdminMapper;
import com.cooking.service.AdminService;
import com.cooking.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public AdminLoginResultDTO login(AdminLoginDTO adminLoginDTO) {
        String username = adminLoginDTO.getUsername();
        String password = adminLoginDTO.getPassword();
        
        // 根据用户名查询管理员
        Admin admin = getByUsername(username);
        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查账号状态
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String encode = passwordEncoder.encode(password);
        // 验证密码
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("type", "admin"); // 标识为管理员token
        
        String token = JwtUtil.createJWT(claims);
        
        // 更新最后登录时间
        updateLastLoginTime(admin.getId());
        
        // 构建返回结果
        AdminLoginResultDTO result = new AdminLoginResultDTO();
        result.setToken(token);
        result.setAdminId(admin.getId());
        result.setUsername(admin.getUsername());
        result.setName(admin.getName());
        
        log.info("管理员登录成功：{}", username);
        return result;
    }
    
    @Override
    public Admin getByUsername(String username) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        return getOne(queryWrapper);
    }
    
    @Override
    public void updateLastLoginTime(Long adminId) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginTime(LocalDateTime.now());
        updateById(admin);
    }
}