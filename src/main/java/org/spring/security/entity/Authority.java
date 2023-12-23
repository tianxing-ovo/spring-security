package org.spring.security.entity;


import lombok.Data;


@Data
public class Authority {
    private Long id;
    private String authorityName; //权限名称
    private String authorityDescription; //权限描述
}
