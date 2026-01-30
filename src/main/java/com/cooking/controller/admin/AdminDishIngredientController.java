package com.cooking.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cooking.common.Result;
import com.cooking.entity.DishIngredient;
import com.cooking.service.DishIngredientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理-菜品食材Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dish-ingredient")
public class AdminDishIngredientController {

    @Autowired
    private DishIngredientService dishIngredientService;

    /**
     * 根据菜品ID查询食材
     */
    @GetMapping("/list/{dishId}")
    public Result<List<DishIngredient>> getIngredientsByDishId(@PathVariable Long dishId) {
        LambdaQueryWrapper<DishIngredient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishIngredient::getDishId, dishId);
        List<DishIngredient> ingredients = dishIngredientService.list(queryWrapper);
        return Result.success(ingredients);
    }

    /**
     * 批量保存菜品食材
     */
    @PostMapping("/batch-save")
    public Result<String> batchSave(@RequestBody List<DishIngredient> ingredients) {
        try {
            if (ingredients == null || ingredients.isEmpty()) {
                return Result.error("食材列表不能为空");
            }

            Long dishId = ingredients.get(0).getDishId();
            if (dishId == null) {
                return Result.error("菜品ID不能为空");
            }

            // 先删除该菜品的所有食材
            LambdaQueryWrapper<DishIngredient> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(DishIngredient::getDishId, dishId);
            dishIngredientService.remove(deleteWrapper);

            // 重新保存食材
            for (DishIngredient ingredient : ingredients) {
                ingredient.setId(null); // 确保是新增
                ingredient.setDishId(dishId);
            }
            
            dishIngredientService.saveBatch(ingredients);
            return Result.success("保存成功");
        } catch (Exception e) {
            log.error("批量保存食材失败：", e);
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除食材
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        dishIngredientService.removeById(id);
        return Result.success("删除成功");
    }
}