package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜品食材实体类
 */
@Data
@TableName("dish_ingredient")
public class DishIngredient {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dishId;

    private String name;

    private String amount;

    private String unit;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
