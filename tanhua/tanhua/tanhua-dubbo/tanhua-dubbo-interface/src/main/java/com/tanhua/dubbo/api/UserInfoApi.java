package com.tanhua.dubbo.api;

import com.tanhua.model.domian.UserInfo;

public interface UserInfoApi {
    public void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(Long id);
}