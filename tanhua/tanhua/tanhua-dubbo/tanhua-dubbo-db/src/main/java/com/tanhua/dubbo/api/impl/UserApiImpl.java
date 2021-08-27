package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domian.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserApiImpl implements UserApi {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        //创建Wrapper对象
        QueryWrapper<User> qw = new QueryWrapper<>();
        //给Wrapper写入查询条件

        qw.eq("mobile", mobile);
        //讲查询结果返回
        return userMapper.selectOne(qw);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
