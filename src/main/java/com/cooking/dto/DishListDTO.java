package com.cooking.dto;

import lombok.Data;

/**
 * 菜品列表DTO
 */
@Data
public class DishListDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String image;
    private String description;
    private Integer difficulty;
    private Integer cookingTime;
    private Integer collectCount;
    private Boolean isFavorite;
}
