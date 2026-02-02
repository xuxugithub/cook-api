package com.cooking.service.impl;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.UserViewHistory;
import com.cooking.mapper.UserViewHistoryMapper;
import com.cooking.service.UserViewHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 用户浏览历史Service实现类
 */
@Service
public class UserViewHistoryServiceImpl extends ServiceImpl<UserViewHistoryMapper, UserViewHistory> implements UserViewHistoryService {

    // 15分钟的时间间隔（分钟）
    private static final int VIEW_INTERVAL_MINUTES = 15;

    @Override
    @Transactional
    public boolean recordViewHistory(Long userId, Long dishId) {
        if (userId == null || dishId == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 查询是否已有浏览记录
        LambdaQueryWrapper<UserViewHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserViewHistory::getUserId, userId)
               .eq(UserViewHistory::getDishId, dishId);
        
        UserViewHistory existingHistory = getOne(wrapper);
        
        if (existingHistory != null) {
            // 检查距离上次浏览是否超过15分钟
            LocalDateTime lastViewTime = existingHistory.getLastViewTime();
            long minutesBetween = ChronoUnit.MINUTES.between(lastViewTime, now);
            
            if (minutesBetween >= VIEW_INTERVAL_MINUTES) {
                // 超过15分钟，算作新的浏览，更新浏览次数和最后浏览时间
                existingHistory.setViewCount(existingHistory.getViewCount() + 1);
                existingHistory.setLastViewTime(now);
                existingHistory.setUpdateTime(now);
                updateById(existingHistory);
                return true; // 应该增加菜品浏览次数
            } else {
                // 15分钟内，只更新最后浏览时间，不增加浏览次数
                existingHistory.setLastViewTime(now);
                existingHistory.setUpdateTime(now);
                updateById(existingHistory);
                return false; // 不应该增加菜品浏览次数
            }
        } else {
            // 创建新的浏览记录
            UserViewHistory newHistory = new UserViewHistory();
            newHistory.setUserId(userId);
            newHistory.setDishId(dishId);
            newHistory.setViewCount(1);
            newHistory.setLastViewTime(now);
            newHistory.setCreateTime(now);
            newHistory.setUpdateTime(now);
            save(newHistory);
            return true; // 应该增加菜品浏览次数
        }
    }

    @Override
    public List<Long> getUserMostViewedDishIds(Long userId, Integer limit) {
        if (userId == null) {
            return ListUtil.empty();
        }
        return baseMapper.getUserMostViewedDishIds(userId, limit);
    }
}