package com.cooking.controller.app;

import com.cooking.common.Result;
import com.cooking.dto.DishListDTO;
import com.cooking.entity.User;
import com.cooking.service.UserFavoriteService;
import com.cooking.service.UserFollowService;
import com.cooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小程序-用户主页Controller
 */
@RestController
@RequestMapping("/api/app/user/profile")
public class AppUserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFavoriteService userFavoriteService;

    @Autowired
    private UserFollowService userFollowService;

    /**
     * 获取用户主页信息
     */
    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getUserProfile(@PathVariable Long userId,
                                                       HttpServletRequest request) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 尝试从请求属性中获取当前用户ID（如果已登录）
        Long currentUserId = null;
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            currentUserId = (Long) userIdObj;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("nickName", user.getNickName());
        result.put("avatar", user.getAvatarUrl());
        
        // 判断当前用户是否关注了该用户
        if (currentUserId != null) {
            result.put("isFollowed", userFollowService.isFollowed(currentUserId, userId));
        } else {
            result.put("isFollowed", false);
        }

        return Result.success(result);
    }

    /**
     * 获取用户收藏的菜品列表
     */
    @GetMapping("/{userId}/favorites")
    public Result<List<DishListDTO>> getUserFavorites(@PathVariable Long userId) {
        List<DishListDTO> list = userFavoriteService.getFavoriteList(userId);
        return Result.success(list);
    }
}
