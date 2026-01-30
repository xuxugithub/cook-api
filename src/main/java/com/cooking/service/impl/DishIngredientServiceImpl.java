package com.cooking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.DishIngredient;
import com.cooking.mapper.DishIngredientMapper;
import com.cooking.service.DishIngredientService;
import org.springframework.stereotype.Service;

/**
 * 菜品食材Service实现类
 */
@Service
public class DishIngredientServiceImpl extends ServiceImpl<DishIngredientMapper, DishIngredient> implements DishIngredientService {
}