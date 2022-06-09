package com.clouddisk.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    // 用户实体 用于数据库查询和身份验证
    private Long uid;
    private String username;
    private Integer level;
    private String password;

}
