package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.User;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据openid查询用户
     *
     * @param openid 微信openid
     * @return 用户信息
     */
    User getByOpenid(String openid);

    /**
     * 微信登录或注册
     *
     * @param openid 微信openid
     * @param nickname 昵称
     * @param avatar 头像
     * @return 用户信息
     */
    User loginOrRegister(String openid, String nickname, String avatar);
    
    /**
     * 根据openid查找或创建用户
     */
    User findOrCreateUser(String openid, String nickName, String avatarUrl);

    /**
     * 获取用户详细信息（包含统计数据）
     */
    User getUserWithStats(Long userId);

    /**
     * 更新用户粉丝数
     */
    void updateFansCount(Long userId, int increment);

    /**
     * 更新用户关注数
     */
    void updateFollowCount(Long userId, int increment);
    
    /**
     * 根据手机号查找用户
     */
    User findByPhone(String phone);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
}
