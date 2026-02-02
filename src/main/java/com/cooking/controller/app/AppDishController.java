package com.cooking.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cooking.common.Result;
import com.cooking.dto.DishDetailDTO;
import com.cooking.dto.DishListDTO;
import com.cooking.dto.FavoriteResultDTO;
import com.cooking.service.DishService;
import com.cooking.service.UserFavoriteService;
import com.cooking.service.UserViewHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 小程序-菜品Controller
 */
@RestController
@RequestMapping("/api/app/dish")
public class AppDishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private UserFavoriteService userFavoriteService;

    @Autowired
    private UserViewHistoryService userViewHistoryService;

    /**
     * 分页查询菜品列表
     */
    @GetMapping("/page")
    public Result<Page<DishListDTO>> page(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Long userId) {
        Page<DishListDTO> page = dishService.pageDishList(categoryId, current, size, userId);
        return Result.success(page);
    }

    /**
     * 获取菜品详情
     */
    @GetMapping("/{id}")
    public Result<DishDetailDTO> getDetail(@PathVariable Long id,
                                           @RequestParam(required = false) Long userId,
                                           HttpServletRequest request) {
        // 获取当前用户ID
        Long currentUserId = userId;
        if (currentUserId == null) {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj != null) {
                currentUserId = (Long) userIdObj;
            }
        }
        
        // 记录用户浏览历史（如果已登录），并根据15分钟规则判断是否增加浏览次数
        boolean shouldIncrementViewCount = false;
        if (currentUserId != null) {
            // 记录浏览历史，返回是否应该增加浏览次数（15分钟内同一用户同一菜品只算一次）
            shouldIncrementViewCount = userViewHistoryService.recordViewHistory(currentUserId, id);
        } else {
            // 未登录用户，直接增加浏览次数（无法判断15分钟规则）
            shouldIncrementViewCount = true;
        }
        
        // 只有在应该增加浏览次数时才增加
        if (shouldIncrementViewCount) {
            dishService.incrementViewCount(id);
        }
        
        DishDetailDTO detail = dishService.getDishDetail(id, currentUserId);
        return Result.success(detail);
    }

    /**
     * 分享菜品
     */
    @PostMapping("/share/{id}")
    public Result<String> share(@PathVariable Long id) {
        dishService.incrementShareCount(id);
        return Result.success("分享成功");
    }

    /**
     * 搜索菜品（根据菜品名称模糊搜索，公开接口，支持可选登录）
     */
    @GetMapping("/search")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.cooking.dto.DishListDTO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.cooking.dto.DishListDTO> page = 
                dishService.searchDish(keyword, current, size, userId);
        return Result.success(page);
    }

    /**
     * 获取热门菜品列表（按浏览量排序）
     */
    @GetMapping("/hot")
    public Result<Page<DishListDTO>> getHotDishes(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        Page<DishListDTO> page = dishService.getHotDishes(current, size, userId);
        return Result.success(page);
    }

    /**
     * 获取推荐菜品列表（按创建时间排序）
     */
    @GetMapping("/recommend")
    public Result<Page<DishListDTO>> getRecommendDishes(@RequestParam(defaultValue = "1") Integer current,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        Page<DishListDTO> page = dishService.getRecommendDishes(current, size, userId);
        return Result.success(page);
    }

    /**
     * 根据分类ID获取菜品列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<Page<DishListDTO>> getDishByCategory(@PathVariable Long categoryId,
                                                       @RequestParam(defaultValue = "1") Integer current,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        Page<DishListDTO> page = dishService.pageDishList(categoryId, current, size, userId);
        return Result.success(page);
    }

    /**
     * 增加菜品浏览量
     */
    @PostMapping("/{id}/view")
    public Result<String> increaseViewCount(@PathVariable Long id) {
        dishService.incrementViewCount(id);
        return Result.success("浏览量增加成功");
    }

    /**
     * 记录用户浏览历史（有效浏览）
     */
    @PostMapping("/{id}/view-history")
    public Result<String> recordViewHistory(@PathVariable Long id, HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        
        // 记录用户浏览历史，并根据15分钟规则判断是否增加浏览次数
        boolean shouldIncrementViewCount;
        if (userId != null) {
            // 记录浏览历史，返回是否应该增加浏览次数（15分钟内同一用户同一菜品只算一次）
            shouldIncrementViewCount = userViewHistoryService.recordViewHistory(userId, id);
        } else {
            // 未登录用户，直接增加浏览次数（无法判断15分钟规则）
            shouldIncrementViewCount = true;
        }
        
        // 只有在应该增加浏览次数时才增加
        if (shouldIncrementViewCount) {
            dishService.incrementViewCount(id);
        }
        
        if (userId != null) {
            return Result.success("浏览历史记录成功");
        } else {
            return Result.success("未登录用户，仅记录浏览量");
        }
    }

    /**
     * 收藏/取消收藏菜品
     */
    @PostMapping("/{id}/favorite")
    public Result<com.cooking.dto.FavoriteResultDTO> toggleFavorite(@PathVariable Long id, HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        
        if (userId == null) {
            // 如果没有登录，返回默认状态（小程序使用本地存储）
            return Result.success(new FavoriteResultDTO("操作成功", false, 0));
        }
        
        try {
            FavoriteResultDTO result = userFavoriteService.toggleFavorite(userId, id);
            return Result.success(result);
        } catch (Exception e) {
            // 如果操作失败，返回错误信息
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 获取个人推荐菜品列表（基于用户浏览历史）
     */
    @GetMapping("/personal-recommend")
    public Result<Page<DishListDTO>> getPersonalRecommendDishes(@RequestParam(defaultValue = "1") Integer current,
                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        Page<DishListDTO> page = dishService.getPersonalRecommendDishes(userId, current, size);
        return Result.success(page);
    }

    /**
     * 获取所有菜品列表（支持多种排序）
     */
    @GetMapping("/all")
    public Result<Page<DishListDTO>> getAllDishes(@RequestParam(defaultValue = "collect") String sortType,
                                                  @RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long userId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            userId = (Long) userIdObj;
        }
        Page<DishListDTO> page = dishService.getAllDishes(sortType, current, size, userId);
        return Result.success(page);
    }
}
