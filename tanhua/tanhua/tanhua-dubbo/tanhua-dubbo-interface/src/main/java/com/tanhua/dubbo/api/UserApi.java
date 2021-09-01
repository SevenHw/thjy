package com.tanhua.dubbo.api;

import com.tanhua.model.domian.User;

public interface UserApi {
    User findByMobile(String mobile);

    Long save(User user);


    void update(String phone, Long userId);


    User findById(Long userId);
}
