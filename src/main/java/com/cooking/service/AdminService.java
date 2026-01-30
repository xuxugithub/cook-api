package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.dto.AdminLoginDTO;
import com.cooking.dto.AdminLoginResultDTO;
import com.cooking.entity.Admin;

/**
 * 管理员服务接口
 */
public interface AdminService extends IService<Admin> {
    
    /**
     * 管理员登录
     * @param adminLoginDTO 登录信息
     * @return 登录结果
     */
    AdminLoginResultDTO login(AdminLoginDTO adminLoginDTO);
    
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员信息
     */
    Admin getByUsername(String username);
    
    /**
     * 更新最后登录时间
     * @param adminId 管理员ID
     */
    void updateLastLoginTime(Long adminId);
}