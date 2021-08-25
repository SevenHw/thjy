package com.itcast.user.service;


import com.itcast.user.domain.User;

public interface UserService {

    User queryById(Long id);
}
