package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
