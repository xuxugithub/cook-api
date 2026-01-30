package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.dto.DishListDTO;
import com.cooking.dto.FavoriteResultDTO;
import com.cooking.entity.UserFavorite;

import java.util.List;

/**
 * 用户收藏Service接口
 */
public interface UserFavoriteService extends IService<UserFavorite> {
    /**
     * 收藏菜品
     */
    void addFavorite(Long userId, Long dishId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long dishId);

    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    FavoriteResultDTO toggleFavorite(Long userId, Long dishId);

    /**
     * 判断是否已收藏
     */
    Boolean isFavorite(Long userId, Long dishId);

    /**
     * 获取用户收藏列表
     */
    List<DishListDTO> getFavoriteList(Long userId);
}
