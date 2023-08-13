package org.spring.security.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;


@ApiModel("权限类")
@Data
public class Authority {
    private Long id;
    private String authorityName; //权限名称
    private String authorityDescription; //权限描述
}
