package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.UserViewHistory;

import java.util.List;

/**
 * 用户浏览历史Service接口
 */
public interface UserViewHistoryService extends IService<UserViewHistory> {
    
    /**
     * 记录用户浏览历史（15分钟内同一用户同一菜品只算一次浏览）
     * @param userId 用户ID
     * @param dishId 菜品ID
     * @return 是否应该增加菜品浏览次数（true-应该增加，false-15分钟内已浏览过）
     */
    boolean recordViewHistory(Long userId, Long dishId);
    
    /**
     * 获取用户浏览最多的菜品ID列表
     */
    List<Long> getUserMostViewedDishIds(Long userId, Integer limit);
}