package com.cooking.controller.admin;

import com.cooking.common.Result;
import com.cooking.entity.Admin;
import com.cooking.service.AdminService;
import com.cooking.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminInfoController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<Admin> getCurrentAdminInfo(HttpServletRequest request) {
        try {
            // 从请求属性中获取管理员ID（由拦截器设置）
            Long adminId = (Long) request.getAttribute("adminId");
            
            Admin admin = adminService.getById(adminId);
            if (admin != null) {
                // 不返回密码信息
                admin.setPassword(null);
            }
            
            return Result.success(admin);
        } catch (Exception e) {
            log.error("获取管理员信息失败：", e);
            return Result.error("获取管理员信息失败");
        }
    }
    
    /**
     * 修改管理员信息
     */
    @PostMapping("/update")
    public Result<String> updateAdminInfo(@RequestBody Admin admin, HttpServletRequest request) {
        try {
            // 从请求属性中获取管理员ID
            Long adminId = (Long) request.getAttribute("adminId");
            
            // 只允许修改部分字段
            Admin updateAdmin = new Admin();
            updateAdmin.setId(adminId);
            updateAdmin.setName(admin.getName());
            updateAdmin.setPhone(admin.getPhone());
            updateAdmin.setEmail(admin.getEmail());
            
            adminService.updateById(updateAdmin);
            return Result.success("修改成功");
        } catch (Exception e) {
            log.error("修改管理员信息失败：", e);
            return Result.error("修改失败");
        }
    }
}