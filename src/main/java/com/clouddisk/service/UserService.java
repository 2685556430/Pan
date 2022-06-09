package com.clouddisk.service;

import com.clouddisk.pojo.User;

public interface UserService {
    // 通过uid查询用户实体
    User getUserByUid(Long uid);
    User getUserByName(String username);
    void insertUser(User user);

}
