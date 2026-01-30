package com.cooking.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cooking.common.Result;
import com.cooking.entity.DishIngredient;
import com.cooking.service.DishIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序-菜品食材Controller
 */
@RestController
@RequestMapping("/api/app/dish-ingredient")
public class AppDishIngredientController {

    @Autowired
    private DishIngredientService dishIngredientService;

    /**
     * 根据菜品ID获取食材列表
     */
    @GetMapping("/list/{dishId}")
    public Result<List<DishIngredient>> getByDishId(@PathVariable Long dishId) {
        LambdaQueryWrapper<DishIngredient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishIngredient::getDishId, dishId)
               .orderByAsc(DishIngredient::getId);
        List<DishIngredient> list = dishIngredientService.list(wrapper);
        return Result.success(list);
    }
}