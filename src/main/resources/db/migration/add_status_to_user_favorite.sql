-- 为user_favorite表添加status字段
-- 用于标识收藏状态：1-已收藏，0-已取消收藏

-- 添加status字段
ALTER TABLE user_favorite ADD COLUMN status INT DEFAULT 1 COMMENT '收藏状态：1-已收藏，0-已取消收藏';

-- 为现有数据设置默认状态为已收藏
UPDATE user_favorite SET status = 1 WHERE status IS NULL;

-- 添加索引以提高查询性能
CREATE INDEX idx_user_favorite_status ON user_favorite(user_id, dish_id, status);

-- 添加唯一约束，确保同一用户对同一菜品只能有一条记录
ALTER TABLE user_favorite ADD CONSTRAINT uk_user_dish UNIQUE (user_id, dish_id);