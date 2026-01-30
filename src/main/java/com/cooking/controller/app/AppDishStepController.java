package com.cooking.controller.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cooking.common.Result;
import com.cooking.entity.DishStep;
import com.cooking.service.DishStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序-菜品制作步骤Controller
 */
@RestController
@RequestMapping("/api/app/dish-step")
public class AppDishStepController {

    @Autowired
    private DishStepService dishStepService;

    /**
     * 根据菜品ID获取制作步骤列表
     */
    @GetMapping("/list/{dishId}")
    public Result<List<DishStep>> getByDishId(@PathVariable Long dishId) {
        LambdaQueryWrapper<DishStep> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishStep::getDishId, dishId)
               .orderByAsc(DishStep::getStepNumber);
        List<DishStep> list = dishStepService.list(wrapper);
        return Result.success(list);
    }
}