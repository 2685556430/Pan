package com.clouddisk.service.serviceImpl;

import com.clouddisk.mapper.UserMapper;
import com.clouddisk.pojo.User;
import com.clouddisk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public User getUserByUid(Long uid) {
        return userMapper.getUserByUid(uid);
    }

    @Override
    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }
}
