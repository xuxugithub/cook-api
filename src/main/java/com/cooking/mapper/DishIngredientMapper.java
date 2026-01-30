package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.DishIngredient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品食材Mapper
 */
@Mapper
public interface DishIngredientMapper extends BaseMapper<DishIngredient> {
}
