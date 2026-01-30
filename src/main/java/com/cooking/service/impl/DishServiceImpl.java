package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cooking.dto.DishDetailDTO;
import com.cooking.dto.DishIngredientDTO;
import com.cooking.dto.DishListDTO;
import com.cooking.dto.DishStepDTO;
import com.cooking.entity.*;
import com.cooking.mapper.*;
import com.cooking.service.DishService;
import com.cooking.service.UserFavoriteService;
import com.cooking.service.UserViewHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品Service实现类
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishIngredientMapper dishIngredientMapper;

    @Autowired
    private DishStepMapper dishStepMapper;

    @Autowired
    private UserFavoriteService userFavoriteService;

    @Autowired
    private UserViewHistoryService userViewHistoryService;

    @Override
    public Page<DishListDTO> pageDishList(Long categoryId, Integer current, Integer size, Long userId) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Dish::getCategoryId, categoryId)
               .eq(Dish::getStatus, 1)
               .orderByDesc(Dish::getCreateTime);

        Page<Dish> dishPage = page(page, wrapper);
        Page<DishListDTO> resultPage = new Page<>(current, size, dishPage.getTotal());

        List<DishListDTO> list = dishPage.getRecords().stream().map(dish -> {
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            // 查询是否收藏
            if (userId != null) {
                dto.setIsFavorite(userFavoriteService.isFavorite(userId, dish.getId()));
            } else {
                dto.setIsFavorite(false);
            }
            
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(list);
        return resultPage;
    }

    @Override
    public DishDetailDTO getDishDetail(Long dishId, Long userId) {
        Dish dish = getById(dishId);
        if (dish == null || dish.getStatus() == 0) {
            throw new RuntimeException("菜品不存在或已下架");
        }

        DishDetailDTO dto = new DishDetailDTO();
        BeanUtils.copyProperties(dish, dto);

        // 查询分类名称
        Category category = categoryMapper.selectById(dish.getCategoryId());
        if (category != null) {
            dto.setCategoryName(category.getName());
        }

        // 查询食材列表
        LambdaQueryWrapper<DishIngredient> ingredientWrapper = new LambdaQueryWrapper<>();
        ingredientWrapper.eq(DishIngredient::getDishId, dishId)
                        .orderByAsc(DishIngredient::getId);
        List<DishIngredient> ingredients = dishIngredientMapper.selectList(ingredientWrapper);
        List<DishIngredientDTO> ingredientDTOs = ingredients.stream().map(ingredient -> {
            DishIngredientDTO ingredientDTO = new DishIngredientDTO();
            BeanUtils.copyProperties(ingredient, ingredientDTO);
            return ingredientDTO;
        }).collect(Collectors.toList());
        dto.setIngredients(ingredientDTOs);

        // 查询制作步骤
        LambdaQueryWrapper<DishStep> stepWrapper = new LambdaQueryWrapper<>();
        stepWrapper.eq(DishStep::getDishId, dishId)
                  .orderByAsc(DishStep::getStepNumber);
        List<DishStep> steps = dishStepMapper.selectList(stepWrapper);
        List<DishStepDTO> stepDTOs = steps.stream().map(step -> {
            DishStepDTO stepDTO = new DishStepDTO();
            BeanUtils.copyProperties(step, stepDTO);
            return stepDTO;
        }).collect(Collectors.toList());
        dto.setSteps(stepDTOs);

        // 查询是否收藏
        if (userId != null) {
            dto.setIsFavorite(userFavoriteService.isFavorite(userId, dishId));
        } else {
            dto.setIsFavorite(false);
        }

        return dto;
    }

    @Override
    @Transactional
    public void incrementViewCount(Long dishId) {
        Dish dish = getById(dishId);
        if (dish != null) {
            dish.setViewCount(dish.getViewCount() + 1);
            updateById(dish);
        }
    }

    @Override
    @Transactional
    public void incrementShareCount(Long dishId) {
        Dish dish = getById(dishId);
        if (dish != null) {
            dish.setShareCount(dish.getShareCount() + 1);
            updateById(dish);
        }
    }

    @Override
    public Page<DishListDTO> searchDish(String keyword, Integer current, Integer size, Long userId) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.trim().isEmpty(), Dish::getName, keyword)
               .eq(Dish::getStatus, 1)
               .orderByDesc(Dish::getCreateTime);

        Page<Dish> dishPage = page(page, wrapper);
        Page<DishListDTO> resultPage = new Page<>(current, size, dishPage.getTotal());

        List<DishListDTO> list = dishPage.getRecords().stream().map(dish -> {
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            // 查询是否收藏
            if (userId != null) {
                dto.setIsFavorite(userFavoriteService.isFavorite(userId, dish.getId()));
            } else {
                dto.setIsFavorite(false);
            }
            
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(list);
        return resultPage;
    }

    @Override
    public Page<DishListDTO> getHotDishes(Integer current, Integer size, Long userId) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1)
               .orderByDesc(Dish::getCollectCount)  // 改为按收藏数排序
               .orderByDesc(Dish::getViewCount)     // 然后按浏览量排序
               .orderByDesc(Dish::getCreateTime);

        Page<Dish> dishPage = page(page, wrapper);
        Page<DishListDTO> resultPage = new Page<>(current, size, dishPage.getTotal());

        List<DishListDTO> list = dishPage.getRecords().stream().map(dish -> {
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            // 查询是否收藏
            if (userId != null) {
                dto.setIsFavorite(userFavoriteService.isFavorite(userId, dish.getId()));
            } else {
                dto.setIsFavorite(false);
            }
            
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(list);
        return resultPage;
    }

    @Override
    public Page<DishListDTO> getRecommendDishes(Integer current, Integer size, Long userId) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1)
               .orderByDesc(Dish::getCreateTime);

        Page<Dish> dishPage = page(page, wrapper);
        Page<DishListDTO> resultPage = new Page<>(current, size, dishPage.getTotal());

        List<DishListDTO> list = dishPage.getRecords().stream().map(dish -> {
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            // 查询是否收藏
            if (userId != null) {
                dto.setIsFavorite(userFavoriteService.isFavorite(userId, dish.getId()));
            } else {
                dto.setIsFavorite(false);
            }
            
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(list);
        return resultPage;
    }

    @Override
    public Page<DishListDTO> getPersonalRecommendDishes(Long userId, Integer current, Integer size) {
        if (userId == null) {
            // 如果用户未登录，返回热门菜品
            return getHotDishes(current, size, null);
        }

        // 获取用户最近浏览的菜品ID列表（按最后浏览时间排序）
        List<Long> recentViewedDishIds = userViewHistoryService.getUserMostViewedDishIds(userId, 20);
        
        if (recentViewedDishIds.isEmpty()) {
            // 如果用户没有浏览历史，返回热门菜品
            return getHotDishes(current, size, userId);
        }

        // 根据用户最近浏览历史推荐相似菜品（同分类或相关菜品）
        Page<Dish> page = new Page<>(current, size);
        
        // 获取用户最近浏览过的菜品的分类ID（按浏览时间排序，最近的优先）
        List<Dish> recentViewedDishes = listByIds(recentViewedDishIds);
        List<Long> categoryIds = recentViewedDishes.stream()
                .map(Dish::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1)
               .in(!categoryIds.isEmpty(), Dish::getCategoryId, categoryIds)
               .notIn(Dish::getId, recentViewedDishIds) // 排除已浏览的菜品
               .orderByDesc(Dish::getCollectCount)      // 优先推荐收藏多的
               .orderByDesc(Dish::getViewCount)         // 然后按浏览量
               .orderByDesc(Dish::getCreateTime);       // 最后按创建时间

        Page<Dish> dishPage = page(page, wrapper);
        Page<DishListDTO> resultPage = new Page<>(current, size, dishPage.getTotal());

        List<DishListDTO> list = dishPage.getRecords().stream().map(dish -> {
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            // 查询是否收藏
            dto.setIsFavorite(userFavoriteService.isFavorite(userId, dish.getId()));
            
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(list);
        return resultPage;
    }

    @Override
    public Page<DishListDTO> getAllDishes(String sortType, Integer current, Integer size, Long userId) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1);

        // 根据排序类型设置排序规则
        switch (sortType) {
            case "collect":
                // 收藏最多
                wrapper.orderByDesc(Dish::getCollectCount)
                       .orderByDesc(Dish::getCreateTime);
                break;
            case "view":
                // 浏览最多
                wrapper.orderByDesc(Dish::getViewCount)
                       .orderByDesc(Dish::getCreateTime);
                break;
            case "latest":
                // 最新上架
                wrapper.orderByDesc(Dish::getCreateTime);
                break;
            default:
                // 默认按收藏数排序
                wrapper.orderByDesc(Dish::getCollectCount)
                       .orderByDesc(Dish::getCreateTime);
                break;
        }

        Page<Dish> dishPage = page(page, wrapper);
        Page<DishListDTO> resultPage = new Page<>(current, size, dishPage.getTotal());

        List<DishListDTO> list = dishPage.getRecords().stream().map(dish -> {
            DishListDTO dto = new DishListDTO();
            BeanUtils.copyProperties(dish, dto);
            
            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            
            // 查询是否收藏
            if (userId != null) {
                dto.setIsFavorite(userFavoriteService.isFavorite(userId, dish.getId()));
            } else {
                dto.setIsFavorite(false);
            }
            
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(list);
        return resultPage;
    }
}
