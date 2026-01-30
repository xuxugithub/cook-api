package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户关注Mapper
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {
}
