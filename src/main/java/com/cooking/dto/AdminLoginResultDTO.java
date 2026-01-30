package com.cooking.dto;

import lombok.Data;

/**
 * 管理员登录结果DTO
 */
@Data
public class AdminLoginResultDTO {
    
    /**
     * JWT Token
     */
    private String token;
    
    /**
     * 管理员ID
     */
    private Long adminId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
}