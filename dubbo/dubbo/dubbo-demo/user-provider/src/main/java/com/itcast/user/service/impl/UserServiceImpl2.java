package com.itcast.user.service.impl;


import com.itcast.user.domain.User;
import com.itcast.user.mapper.UserMapper;
import com.itcast.user.service.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "2.0.0")
public class UserServiceImpl2 implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryById(Long id) {
        return userMapper.findById(id);
    }
}