package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.dto.DishListDTO;
import com.cooking.dto.FavoriteResultDTO;
import com.cooking.entity.Category;
import com.cooking.entity.Dish;
import com.cooking.entity.UserFavorite;
import com.cooking.mapper.CategoryMapper;
import com.cooking.mapper.DishMapper;
import com.cooking.mapper.UserFavoriteMapper;
import com.cooking.service.UserFavoriteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户收藏Service实现类
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void addFavorite(Long userId, Long dishId) {
        // 检查是否已存在收藏记录
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getDishId, dishId);
        UserFavorite favorite = getOne(wrapper);
        
        if (favorite != null) {
            if (favorite.getStatus() == 1) {
                throw new RuntimeException("该菜品已收藏");
            } else {
                // 更新状态为已收藏
                favorite.setStatus(1);
                updateById(favorite);
            }
        } else {
            // 创建新的收藏记录
            UserFavorite newFavorite = new UserFavorite();
            newFavorite.setUserId(userId);
            newFavorite.setDishId(dishId);
            newFavorite.setStatus(1);
            save(newFavorite);
        }

        // 更新菜品收藏数
        Dish dish = dishMapper.selectById(dishId);
        if (dish != null) {
            dish.setCollectCount(dish.getCollectCount() + 1);
            dishMapper.updateById(dish);
        }
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long dishId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getDishId, dishId);
        UserFavorite favorite = getOne(wrapper);
        
        if (favorite == null || favorite.getStatus() == 0) {
            throw new RuntimeException("该菜品未收藏");
        }

        // 更新状态为已取消收藏
        favorite.setStatus(0);
        updateById(favorite);

        // 更新菜品收藏数
        Dish dish = dishMapper.selectById(dishId);
        if (dish != null && dish.getCollectCount() > 0) {
            dish.setCollectCount(dish.getCollectCount() - 1);
            dishMapper.updateById(dish);
        }
    }

    @Override
    @Transactional
    public FavoriteResultDTO toggleFavorite(Long userId, Long dishId) {
        // 检查是否已存在收藏记录
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getDishId, dishId);
        UserFavorite favorite = getOne(wrapper);
        
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new RuntimeException("菜品不存在");
        }
        
        if (favorite != null && favorite.getStatus() == 1) {
            // 已收藏，执行取消收藏
            favorite.setStatus(0);
            updateById(favorite);
            
            // 更新菜品收藏数
            if (dish.getCollectCount() > 0) {
                dish.setCollectCount(dish.getCollectCount() - 1);
                dishMapper.updateById(dish);
            }
            
            return new FavoriteResultDTO("取消收藏成功", false, dish.getCollectCount());
        } else {
            // 未收藏，执行收藏
            if (favorite != null) {
                // 更新现有记录状态
                favorite.setStatus(1);
                updateById(favorite);
            } else {
                // 创建新的收藏记录
                UserFavorite newFavorite = new UserFavorite();
                newFavorite.setUserId(userId);
                newFavorite.setDishId(dishId);
                newFavorite.setStatus(1);
                save(newFavorite);
            }
            
            // 更新菜品收藏数
            dish.setCollectCount(dish.getCollectCount() + 1);
            dishMapper.updateById(dish);
            
            return new FavoriteResultDTO("收藏成功", true, dish.getCollectCount());
        }
    }

    @Override
    public Boolean isFavorite(Long userId, Long dishId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getDishId, dishId)
               .eq(UserFavorite::getStatus, 1);
        return count(wrapper) > 0;
    }

    @Override
    public List<DishListDTO> getFavoriteList(Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getStatus, 1)
               .orderByDesc(UserFavorite::getUpdateTime);
        List<UserFavorite> favorites = list(wrapper);

        return favorites.stream().map(favorite -> {
            Dish dish = dishMapper.selectById(favorite.getDishId());
            if (dish == null || dish.getStatus() == 0) {
                return null;
            }
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            dto.setIsFavorite(true);
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
