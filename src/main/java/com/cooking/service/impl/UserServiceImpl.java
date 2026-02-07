package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.User;
import com.cooking.mapper.UserMapper;
import com.cooking.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return getOne(wrapper);
    }

    @Override
    public User loginOrRegister(String openid, String nickname, String avatar) {
        // 查询用户是否存在
        User user = getByOpenid(openid);
        
        if (user == null) {
            // 新用户，自动注册
            user = new User();
            user.setOpenid(openid);
            user.setNickName(nickname);
            user.setAvatarUrl(avatar);
            user.setStatus(1);
            user.setFansCount(0);
            user.setFollowCount(0);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            save(user);
        } else {
            // 老用户，更新信息
            if (nickname != null && !nickname.isEmpty()) {
                user.setNickName(nickname);
            }
            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatarUrl(avatar);
            }
            user.setUpdateTime(LocalDateTime.now());
            updateById(user);
        }
        
        return user;
    }

    @Override
    public User findOrCreateUser(String openid, String nickName, String avatarUrl) {
        User user = this.selectByOpenid(openid);
        if (user != null) {
            // 若用户信息有变化，更新（避免重复更新，可增加字段对比）
            boolean needUpdate = false;
            
            if (nickName != null && !nickName.equals(user.getNickName())) {
                user.setNickName(nickName);
                needUpdate = true;
            }
            
            // 只有当新头像不为空时才更新头像
            if (avatarUrl != null && !avatarUrl.trim().isEmpty() && !avatarUrl.equals(user.getAvatarUrl())) {
                user.setAvatarUrl(avatarUrl);
                needUpdate = true;
            }
            
            if (needUpdate) {
                this.updateById(user);
            }
            return user;
        }
        // 未找到则创建新用户
        User newUser = new User();
        newUser.setOpenid(openid);
        newUser.setNickName(nickName);
        newUser.setAvatarUrl(avatarUrl != null && !avatarUrl.trim().isEmpty() ? avatarUrl : "");

        this.save(newUser);
        return newUser;
    }

    private User selectByOpenid(String openid) {
        return this.lambdaQuery().eq(User::getOpenid, openid).one();
    }

    @Override
    public User getUserWithStats(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return null;
        }
        
        // 这里可以添加实时统计逻辑，目前直接返回数据库中的值
        // 如果需要实时统计，可以查询关注表和粉丝表
        return user;
    }

    @Override
    public void updateFansCount(Long userId, int increment) {
        User user = getById(userId);
        if (user != null) {
            int newCount = Math.max(0, (user.getFansCount() != null ? user.getFansCount() : 0) + increment);
            user.setFansCount(newCount);
            updateById(user);
        }
    }

    @Override
    public void updateFollowCount(Long userId, int increment) {
        User user = getById(userId);
        if (user != null) {
            int newCount = Math.max(0, (user.getFollowCount() != null ? user.getFollowCount() : 0) + increment);
            user.setFollowCount(newCount);
            updateById(user);
        }
    }
    
    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return getOne(wrapper);
    }
    
    @Override
    public User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return getOne(wrapper);
    }
}
