package com.cooking.controller.app;

import com.cooking.common.Result;
import com.cooking.dto.DishListDTO;
import com.cooking.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 小程序-收藏Controller
 */
@RestController
@RequestMapping("/api/app/favorite")
public class AppFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 收藏菜品
     */
    @PostMapping("/add")
    public Result<String> addFavorite(HttpServletRequest request,
                                       @RequestParam Long dishId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        userFavoriteService.addFavorite(userId, dishId);
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     */
    @PostMapping("/remove")
    public Result<String> removeFavorite(HttpServletRequest request,
                                          @RequestParam Long dishId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        userFavoriteService.removeFavorite(userId, dishId);
        return Result.success("取消收藏成功");
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/list")
    public Result<List<DishListDTO>> getFavoriteList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        List<DishListDTO> list = userFavoriteService.getFavoriteList(userId);
        return Result.success(list);
    }

    /**
     * 判断是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> isFavorite(HttpServletRequest request,
                                      @RequestParam Long dishId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.success(false);
        }
        Boolean isFavorite = userFavoriteService.isFavorite(userId, dishId);
        return Result.success(isFavorite);
    }
}
