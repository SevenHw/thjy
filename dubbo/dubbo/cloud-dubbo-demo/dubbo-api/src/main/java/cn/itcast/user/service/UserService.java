package cn.itcast.user.service;


import cn.itcast.dubbo.domain.User;

public interface UserService {

    User queryById(Long id);
}
