package com.cooking.dto;

import lombok.Data;

/**
 * 菜品食材DTO
 */
@Data
public class DishIngredientDTO {
    private Long id;
    private String name;
    private String amount;
    private String unit;
}
