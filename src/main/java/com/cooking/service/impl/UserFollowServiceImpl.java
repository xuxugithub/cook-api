package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.dto.UserFollowDTO;
import com.cooking.entity.User;
import com.cooking.entity.UserFollow;
import com.cooking.mapper.UserFollowMapper;
import com.cooking.mapper.UserMapper;
import com.cooking.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户关注Service实现类
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void follow(Long userId, Long followUserId) {
        if (userId.equals(followUserId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 检查是否已关注
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, userId)
               .eq(UserFollow::getFollowUserId, followUserId);
        UserFollow exist = getOne(wrapper);
        
        if (exist != null) {
            throw new RuntimeException("已关注该用户");
        }

        // 添加关注
        UserFollow userFollow = new UserFollow();
        userFollow.setUserId(userId);
        userFollow.setFollowUserId(followUserId);
        save(userFollow);
    }

    @Override
    @Transactional
    public void unfollow(Long userId, Long followUserId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, userId)
               .eq(UserFollow::getFollowUserId, followUserId);
        UserFollow userFollow = getOne(wrapper);
        
        if (userFollow == null) {
            throw new RuntimeException("未关注该用户");
        }

        removeById(userFollow.getId());
    }

    @Override
    public Boolean isFollowed(Long userId, Long followUserId) {
        if (userId == null || followUserId == null) {
            return false;
        }
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, userId)
               .eq(UserFollow::getFollowUserId, followUserId);
        return count(wrapper) > 0;
    }

    @Override
    public List<UserFollowDTO> getFollowList(Long userId, Long currentUserId) {
        // 查询我关注的用户
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getUserId, userId)
               .orderByDesc(UserFollow::getCreateTime);
        List<UserFollow> follows = list(wrapper);

        return follows.stream().map(follow -> {
            User user = userMapper.selectById(follow.getFollowUserId());
            if (user == null) {
                return null;
            }
            UserFollowDTO dto = new UserFollowDTO();
            dto.setUserId(user.getId());
            dto.setNickName(user.getNickName());
            dto.setAvatarUrl(user.getAvatarUrl());
            // 判断当前用户是否关注了该用户
            dto.setIsFollowed(isFollowed(currentUserId, user.getId()));
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    @Override
    public List<UserFollowDTO> getFansList(Long userId, Long currentUserId) {
        // 查询关注我的用户（粉丝）
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowUserId, userId)
               .orderByDesc(UserFollow::getCreateTime);
        List<UserFollow> follows = list(wrapper);

        return follows.stream().map(follow -> {
            User user = userMapper.selectById(follow.getUserId());
            if (user == null) {
                return null;
            }
            UserFollowDTO dto = new UserFollowDTO();
            dto.setUserId(user.getId());
            dto.setNickName(user.getNickName());
            dto.setAvatarUrl(user.getAvatarUrl());
            // 判断当前用户是否关注了该用户
            dto.setIsFollowed(isFollowed(currentUserId, user.getId()));
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }
}
