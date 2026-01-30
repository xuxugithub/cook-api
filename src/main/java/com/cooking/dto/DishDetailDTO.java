package com.cooking.dto;

import lombok.Data;
import java.util.List;

/**
 * 菜品详情DTO
 */
@Data
public class DishDetailDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String image;
    private String description;
    private Integer difficulty;
    private Integer cookingTime;
    private Integer servings;
    private Integer calories;
    private Integer viewCount;
    private Integer collectCount;
    private Integer shareCount;
    private Boolean isFavorite;
    private List<DishIngredientDTO> ingredients;
    private List<DishStepDTO> steps;
}
