package com.cooking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cooking.entity.Category;

import java.util.List;

/**
 * 菜单分类Service接口
 */
public interface CategoryService extends IService<Category> {
    /**
     * 获取所有启用的分类列表
     */
    List<Category> listEnabled();
}
