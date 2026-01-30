package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Banner;

import java.util.List;

/**
 * Banner轮播图Service接口
 */
public interface BannerService extends IService<Banner> {
    /**
     * 获取启用的Banner列表（小程序端）
     *
     * @return Banner列表
     */
    List<Banner> listEnabled();
}
