package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.dto.UserFollowDTO;
import com.cooking.entity.UserFollow;

import java.util.List;

/**
 * 用户关注Service接口
 */
public interface UserFollowService extends IService<UserFollow> {
    /**
     * 关注用户
     *
     * @param userId 当前用户ID
     * @param followUserId 被关注用户ID
     */
    void follow(Long userId, Long followUserId);

    /**
     * 取消关注
     *
     * @param userId 当前用户ID
     * @param followUserId 被关注用户ID
     */
    void unfollow(Long userId, Long followUserId);

    /**
     * 判断是否已关注
     *
     * @param userId 当前用户ID
     * @param followUserId 被关注用户ID
     * @return 是否已关注
     */
    Boolean isFollowed(Long userId, Long followUserId);

    /**
     * 获取关注列表（我关注的用户）
     *
     * @param userId 当前用户ID
     * @param currentUserId 查看者用户ID（用于判断是否已关注）
     * @return 关注列表
     */
    List<UserFollowDTO> getFollowList(Long userId, Long currentUserId);

    /**
     * 获取粉丝列表（关注我的用户）
     *
     * @param userId 当前用户ID
     * @param currentUserId 查看者用户ID（用于判断是否已关注）
     * @return 粉丝列表
     */
    List<UserFollowDTO> getFansList(Long userId, Long currentUserId);
}
