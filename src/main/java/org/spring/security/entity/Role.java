package org.spring.security.entity;


import lombok.Data;


@Data
public class Role {
    private Long id;
    private String roleName; //角色名称
    private String roleDescription; //角色描述
}
