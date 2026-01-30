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
import java.util.List;

/**
 * 用户浏览历史Service实现类
 */
@Service
public class UserViewHistoryServiceImpl extends ServiceImpl<UserViewHistoryMapper, UserViewHistory> implements UserViewHistoryService {

    @Override
    @Transactional
    public void recordViewHistory(Long userId, Long dishId) {
        if (userId == null || dishId == null) {
            return;
        }
        
        // 查询是否已有浏览记录
        LambdaQueryWrapper<UserViewHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserViewHistory::getUserId, userId)
               .eq(UserViewHistory::getDishId, dishId);
        
        UserViewHistory existingHistory = getOne(wrapper);
        
        if (existingHistory != null) {
            // 更新浏览次数和最后浏览时间
            existingHistory.setViewCount(existingHistory.getViewCount() + 1);
            existingHistory.setLastViewTime(LocalDateTime.now());
            existingHistory.setUpdateTime(LocalDateTime.now());
            updateById(existingHistory);
        } else {
            // 创建新的浏览记录
            UserViewHistory newHistory = new UserViewHistory();
            newHistory.setUserId(userId);
            newHistory.setDishId(dishId);
            newHistory.setViewCount(1);
            newHistory.setLastViewTime(LocalDateTime.now());
            newHistory.setCreateTime(LocalDateTime.now());
            newHistory.setUpdateTime(LocalDateTime.now());
            save(newHistory);
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