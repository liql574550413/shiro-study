package com.li.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liql
 * @date 2021/5/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String username;
    private String password;
    private String perms; //用户权限
}
