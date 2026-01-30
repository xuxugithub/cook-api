package com.cooking.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cooking.common.Result;
import com.cooking.entity.*;
import com.cooking.mapper.DishIngredientMapper;
import com.cooking.mapper.DishStepMapper;
import com.cooking.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理-菜品Controller
 */
@RestController
@RequestMapping("/api/admin/dish")
public class AdminDishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishStepMapper dishStepMapper;

    @Autowired
    private DishIngredientMapper dishIngredientMapper;

    /**
     * 分页查询菜品列表
     */
    @GetMapping("/page")
    public Result<Page<Dish>> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) Long categoryId) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name)
               .eq(categoryId != null, Dish::getCategoryId, categoryId)
               .orderByDesc(Dish::getCreateTime);
        Page<Dish> result = dishService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 根据ID查询菜品详情（包含步骤和食材）
     */
    @GetMapping("/{id}")
    public Result<Dish> getById(@PathVariable Long id) {
        Dish dish = dishService.getById(id);
        return Result.success(dish);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    @Transactional
    public Result<String> save(@RequestBody Dish dish) {
        dishService.save(dish);
        return Result.success("新增成功");
    }

    /**
     * 更新菜品
     */
    @PutMapping
    @Transactional
    public Result<String> update(@RequestBody Dish dish) {
        dishService.updateById(dish);
        return Result.success("更新成功");
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 上架/下架菜品
     */
    @PutMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Dish dish = dishService.getById(id);
        if (dish == null) {
            return Result.error("菜品不存在");
        }
        dish.setStatus(status);
        dishService.updateById(dish);
        return Result.success(status == 1 ? "上架成功" : "下架成功");
    }
}
