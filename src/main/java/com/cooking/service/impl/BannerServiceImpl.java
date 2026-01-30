package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.Banner;
import com.cooking.mapper.BannerMapper;
import com.cooking.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Banner轮播图Service实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> listEnabled() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1)
               .orderByAsc(Banner::getSort)
               .orderByDesc(Banner::getCreateTime);
        return list(wrapper);
    }
}
