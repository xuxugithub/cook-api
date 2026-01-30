package com.cooking.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cooking.common.Result;
import com.cooking.entity.Banner;
import com.cooking.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理-Banner Controller
 */
@RestController
@RequestMapping("/api/admin/banner")
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 分页查询Banner
     */
    @GetMapping("/page")
    public Result<Page<Banner>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String title) {
        Page<Banner> page = new Page<>(current, size);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null && !title.trim().isEmpty(), Banner::getTitle, title)
               .orderByAsc(Banner::getSort)
               .orderByDesc(Banner::getCreateTime);
        Page<Banner> result = bannerService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有Banner
     */
    @GetMapping("/list")
    public Result<java.util.List<Banner>> list() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Banner::getSort)
               .orderByDesc(Banner::getCreateTime);
        return Result.success(bannerService.list(wrapper));
    }

    /**
     * 查询Banner详情
     */
    @GetMapping("/{id}")
    public Result<Banner> getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("Banner不存在");
        }
        return Result.success(banner);
    }

    /**
     * 新增Banner
     */
    @PostMapping
    public Result<String> save(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success("新增成功");
    }

    /**
     * 更新Banner
     */
    @PutMapping
    public Result<String> update(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return Result.success("更新成功");
    }

    /**
     * 删除Banner
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 启用/禁用Banner
     */
    @PutMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable Long id,
                                       @RequestParam Integer status) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("Banner不存在");
        }
        banner.setStatus(status);
        bannerService.updateById(banner);
        return Result.success("状态更新成功");
    }
}
