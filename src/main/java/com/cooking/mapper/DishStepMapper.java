package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.DishStep;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品制作步骤Mapper
 */
@Mapper
public interface DishStepMapper extends BaseMapper<DishStep> {
}
