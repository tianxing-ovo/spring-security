package com.ltx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ltx.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author tianxing
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
