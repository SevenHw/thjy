package cn.itcast.user.service;


import cn.itcast.user.domain.User;

public interface UserService {

    User queryById(Long id);
}
