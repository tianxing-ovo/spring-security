package org.spring.security.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("角色类")
@Data
public class Role {
    private Long id;
    private String roleName; //角色名称
    private String roleDescription; //角色描述
}
