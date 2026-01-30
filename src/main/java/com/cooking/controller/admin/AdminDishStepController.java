package com.cooking.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cooking.common.Result;
import com.cooking.entity.DishStep;
import com.cooking.service.DishStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台管理-菜品制作步骤Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dish-step")
public class AdminDishStepController {

    @Autowired
    private DishStepService dishStepService;

    /**
     * 根据菜品ID查询制作步骤
     */
    @GetMapping("/list/{dishId}")
    public Result<List<DishStep>> getStepsByDishId(@PathVariable Long dishId) {
        LambdaQueryWrapper<DishStep> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishStep::getDishId, dishId)
                   .orderByAsc(DishStep::getStepNumber);
        List<DishStep> steps = dishStepService.list(queryWrapper);
        return Result.success(steps);
    }

    /**
     * 批量保存菜品制作步骤
     */
    @PostMapping("/batch-save")
    public Result<String> batchSave(@RequestBody List<DishStep> steps) {
        try {
            if (steps == null || steps.isEmpty()) {
                return Result.error("制作步骤不能为空");
            }

            Long dishId = steps.get(0).getDishId();
            if (dishId == null) {
                return Result.error("菜品ID不能为空");
            }

            // 先删除该菜品的所有步骤
            LambdaQueryWrapper<DishStep> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(DishStep::getDishId, dishId);
            dishStepService.remove(deleteWrapper);

            // 重新保存步骤
            for (int i = 0; i < steps.size(); i++) {
                DishStep step = steps.get(i);
                step.setId(null); // 确保是新增
                step.setDishId(dishId);
                step.setStepNumber(i + 1); // 重新设置步骤序号
            }
            
            dishStepService.saveBatch(steps);
            return Result.success("保存成功");
        } catch (Exception e) {
            log.error("批量保存制作步骤失败：", e);
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除制作步骤
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        dishStepService.removeById(id);
        return Result.success("删除成功");
    }
}