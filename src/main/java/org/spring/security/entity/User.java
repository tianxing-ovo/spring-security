package org.spring.security.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@ApiModel("用户类")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 8128222233254275589L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("账号是否未过期")
    private Integer accountNonExpired;

    @ApiModelProperty("账号是否未锁定")
    private Integer accountNonLocked;

    @ApiModelProperty("证书是否未过期")
    private Integer credentialsNonExpired;

    @ApiModelProperty("是否可用")
    private Integer enabled;
}
