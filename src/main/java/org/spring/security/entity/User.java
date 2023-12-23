package org.spring.security.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 8128222233254275589L;


    private Long id;


    private String username;


    private String password;


    private Integer accountNonExpired;


    private Integer accountNonLocked;


    private Integer credentialsNonExpired;

    private Integer enabled;
}
