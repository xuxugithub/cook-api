package com.cooking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.entity.DishStep;
import com.cooking.mapper.DishStepMapper;
import com.cooking.service.DishStepService;
import org.springframework.stereotype.Service;

/**
 * 菜品制作步骤Service实现类
 */
@Service
public class DishStepServiceImpl extends ServiceImpl<DishStepMapper, DishStep> implements DishStepService {
}