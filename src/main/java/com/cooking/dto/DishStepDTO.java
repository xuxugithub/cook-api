package com.cooking.dto;

import lombok.Data;

/**
 * 菜品制作步骤DTO
 */
@Data
public class DishStepDTO {
    private Long id;
    private Integer stepNumber;
    private String description;
    private String image;
}
