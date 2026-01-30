package com.cooking.controller.admin;

import com.cooking.common.Result;
import com.cooking.dto.AdminLoginDTO;
import com.cooking.dto.AdminLoginResultDTO;
import com.cooking.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginResultDTO> login(@Valid @RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("管理员登录：{}", adminLoginDTO.getUsername());
        
        try {
            AdminLoginResultDTO result = adminService.login(adminLoginDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("管理员登录失败：", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 管理员登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // JWT是无状态的，登出只需要前端删除token即可
        return Result.success("登出成功");
    }
}