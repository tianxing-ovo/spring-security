package com.ltx.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tianxing
 */
@Mapper
public interface AuthorityMapper {

    /**
     * 通过用户id获取用户的所有权限
     */
    List<String> getAuthById(@Param("id") Long id);

    /**
     * 通过用户id获取用户的所有角色
     */
    List<String> getRolesById(@Param("id") Long id);
}