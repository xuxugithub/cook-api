package com.cooking.controller.app;

import com.cooking.common.Result;
import com.cooking.entity.Banner;
import com.cooking.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序-Banner Controller
 */
@RestController
@RequestMapping("/api/app/banner")
public class AppBannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取启用的Banner列表
     */
    @GetMapping("/list")
    public Result<List<Banner>> list() {
        List<Banner> list = bannerService.listEnabled();
        return Result.success(list);
    }
}
