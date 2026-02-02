package com.cooking.dto;

import lombok.Data;

/**
 * 用户关注DTO
 */
@Data
public class UserFollowDTO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 是否已关注（当前用户是否关注了该用户）
     */
    private Boolean isFollowed;
}
