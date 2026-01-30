package com.cooking.controller.app;

import com.cooking.common.Result;
import com.cooking.dto.UserFollowDTO;
import com.cooking.service.UserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 小程序-用户关注Controller
 */
@RestController
@RequestMapping("/api/app/follow")
public class AppUserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    /**
     * 关注用户
     */
    @PostMapping("/add")
    public Result<String> follow(HttpServletRequest request,
                                  @RequestParam Long followUserId) {
        Long userId = (Long) request.getAttribute("userId");
        userFollowService.follow(userId, followUserId);
        return Result.success("关注成功");
    }

    /**
     * 取消关注
     */
    @PostMapping("/remove")
    public Result<String> unfollow(HttpServletRequest request,
                                   @RequestParam Long followUserId) {
        Long userId = (Long) request.getAttribute("userId");
        userFollowService.unfollow(userId, followUserId);
        return Result.success("取消关注成功");
    }

    /**
     * 判断是否已关注
     */
    @GetMapping("/check")
    public Result<Boolean> isFollowed(HttpServletRequest request,
                                     @RequestParam Long followUserId) {
        Long userId = (Long) request.getAttribute("userId");
        Boolean isFollowed = userFollowService.isFollowed(userId, followUserId);
        return Result.success(isFollowed);
    }

    /**
     * 获取关注列表（我关注的用户）
     */
    @GetMapping("/list")
    public Result<List<UserFollowDTO>> getFollowList(@RequestParam Long userId,
                                                      HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        List<UserFollowDTO> list = userFollowService.getFollowList(userId, currentUserId);
        return Result.success(list);
    }

    /**
     * 获取粉丝列表（关注我的用户）
     */
    @GetMapping("/fans")
    public Result<List<UserFollowDTO>> getFansList(@RequestParam Long userId,
                                                    HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        List<UserFollowDTO> list = userFollowService.getFansList(userId, currentUserId);
        return Result.success(list);
    }
}
