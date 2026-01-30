package com.cooking.dto;

import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {
    /**
     * 微信登录code
     */
    private String code;

    /**
     * 昵称（可选）
     */
    private String nickname;

    /**
     * 头像（可选）
     */
    private String avatar;
}
