package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.UserViewHistory;

import java.util.List;

/**
 * 用户浏览历史Service接口
 */
public interface UserViewHistoryService extends IService<UserViewHistory> {
    
    /**
     * 记录用户浏览历史
     */
    void recordViewHistory(Long userId, Long dishId);
    
    /**
     * 获取用户浏览最多的菜品ID列表
     */
    List<Long> getUserMostViewedDishIds(Long userId, Integer limit);
}