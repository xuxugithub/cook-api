package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注实体类
 */
@Data
@TableName("user_follow")
public class UserFollow implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关注者）
     */
    private Long userId;

    /**
     * 被关注用户ID
     */
    private Long followUserId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
