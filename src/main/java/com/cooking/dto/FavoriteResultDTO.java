package com.cooking.dto;

import lombok.Data;

/**
 * 收藏操作结果DTO
 */
@Data
public class FavoriteResultDTO {
    /**
     * 操作消息
     */
    private String message;
    
    /**
     * 当前收藏状态
     */
    private Boolean isFavorite;
    
    /**
     * 菜品当前收藏数
     */
    private Integer collectCount;
    
    public FavoriteResultDTO(String message, Boolean isFavorite, Integer collectCount) {
        this.message = message;
        this.isFavorite = isFavorite;
        this.collectCount = collectCount;
    }
}