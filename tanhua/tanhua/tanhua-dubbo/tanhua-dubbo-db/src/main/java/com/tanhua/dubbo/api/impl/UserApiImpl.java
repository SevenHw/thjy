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

    //通过手机号查询
    @Override
    public User findByMobile(String mobile) {
        //创建Wrapper对象
        QueryWrapper<User> qw = new QueryWrapper<>();
        //给Wrapper写入查询条件
        qw.eq("mobile", mobile);
        //讲查询结果返回
        return userMapper.selectOne(qw);
    }

    /**
     * 添加
     *
     * @param user
     * @return
     */
    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 修改手机号码
     *
     * @param phone
     * @param userId
     */
    @Override
    public void update(String phone, Long userId) {
        //创建Wrapper对象
        QueryWrapper<User> qw = new QueryWrapper<>();
        //给Wrapper写入查询条件
        qw.eq("id", userId);
        //创建实体类对象
        User user = new User();
        user.setId(userId);
        user.setMobile(phone);
        userMapper.update(user, qw);
    }


    @Override
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 更新
     *
     * @param user
     */
    @Override
    public void hxUpdate(User user) {
        userMapper.updateById(user);
    }

    /**
     * 通过手机号码查询
     *
     * @param userId
     * @return
     */
    @Override
    public User findHx(Long userId) {
        User user = userMapper.selectById(userId);
        return user;
    }

    /**
     * 根据环信id查询用户
     *
     * @param huanxinId
     * @return
     */
    @Override
    public User findByHxId(String huanxinId) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("hx_user", huanxinId);
        return userMapper.selectOne(qw);
    }


}
