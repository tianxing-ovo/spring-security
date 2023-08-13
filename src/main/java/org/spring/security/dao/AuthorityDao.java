package org.spring.security.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.security.entity.Authority;

import java.util.List;

@Mapper
public interface AuthorityDao extends BaseMapper<Authority> {

    /**
     * 通过用户id获取用户的所有权限
     */
    List<String> getAuthById(@Param("id") Long id);
}