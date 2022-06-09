package com.clouddisk.mapper;

import com.clouddisk.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getUserByUid(Long uid);

    User getUserByName(String username);

    void insertUser(User user);

    void deleteUserByName(String username);

    void updateUserPassword(String username);
}
