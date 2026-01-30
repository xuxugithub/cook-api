-- 为user表添加粉丝数和关注数字段

-- 添加粉丝数字段
ALTER TABLE user ADD COLUMN fans_count INT DEFAULT 0 COMMENT '粉丝数';

-- 添加关注数字段
ALTER TABLE user ADD COLUMN follow_count INT DEFAULT 0 COMMENT '关注数';

-- 为现有用户设置默认值
UPDATE user SET fans_count = 0 WHERE fans_count IS NULL;
UPDATE user SET follow_count = 0 WHERE follow_count IS NULL;