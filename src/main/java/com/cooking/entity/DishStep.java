package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 菜品制作步骤实体类
 */
@Data
@TableName("dish_step")
public class DishStep {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dishId;

    private Integer stepNumber;

    private String description;

    private String image;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
