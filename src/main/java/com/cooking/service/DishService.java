package com.cooking.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.dto.DishDetailDTO;
import com.cooking.dto.DishListDTO;
import com.cooking.entity.Dish;

import java.util.List;

/**
 * 菜品Service接口
 */
public interface DishService extends IService<Dish> {
    /**
     * 分页查询菜品列表
     */
    Page<DishListDTO> pageDishList(Long categoryId, Integer current, Integer size, Long userId);

    /**
     * 获取菜品详情
     */
    DishDetailDTO getDishDetail(Long dishId, Long userId);

    /**
     * 增加菜品浏览次数
     */
    void incrementViewCount(Long dishId);

    /**
     * 增加菜品分享次数
     */
    void incrementShareCount(Long dishId);

    /**
     * 根据菜品名称模糊搜索
     *
     * @param keyword 关键词
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID（用于判断是否收藏）
     * @return 菜品列表
     */
    Page<DishListDTO> searchDish(String keyword, Integer current, Integer size, Long userId);

    /**
     * 获取热门菜品列表（按浏览量排序）
     *
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID（用于判断是否收藏）
     * @return 热门菜品列表
     */
    Page<DishListDTO> getHotDishes(Integer current, Integer size, Long userId);

    /**
     * 获取推荐菜品列表（按创建时间排序）
     *
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID（用于判断是否收藏）
     * @return 推荐菜品列表
     */
    Page<DishListDTO> getRecommendDishes(Integer current, Integer size, Long userId);

    /**
     * 根据用户浏览历史获取个人推荐菜品
     *
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 个人推荐菜品列表
     */
    Page<DishListDTO> getPersonalRecommendDishes(Long userId, Integer current, Integer size);

    /**
     * 获取所有菜品列表（支持多种排序）
     *
     * @param sortType 排序类型：collect-收藏最多，view-浏览最多，latest-最新上架
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID（用于判断是否收藏）
     * @return 菜品列表
     */
    Page<DishListDTO> getAllDishes(String sortType, Integer current, Integer size, Long userId);
}
