package cn.itcast.user.service.impl;


import cn.itcast.user.mapper.UserMapper;
import cn.itcast.user.service.UserService;
import cn.itcast.dubbo.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryById(Long id) {
        return userMapper.findById(id);
    }
}