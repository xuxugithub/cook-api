package com.cooking.controller.app;

import com.cooking.common.Result;
import com.cooking.entity.Category;
import com.cooking.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序-菜单分类Controller
 */
@RestController
@RequestMapping("/api/app/category")
public class AppCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有启用的分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.listEnabled();
        return Result.success(list);
    }
}
