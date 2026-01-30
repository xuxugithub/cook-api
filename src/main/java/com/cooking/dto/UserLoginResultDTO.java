package com.cooking.dto;

import lombok.Data;

/**
 * 用户登录结果DTO
 */
@Data
public class UserLoginResultDTO {
    /**
     * JWT token
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}
