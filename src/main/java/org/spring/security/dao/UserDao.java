package org.spring.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.spring.security.entity.User;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
