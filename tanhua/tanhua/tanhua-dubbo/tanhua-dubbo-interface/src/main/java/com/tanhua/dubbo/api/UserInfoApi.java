package com.tanhua.dubbo.api;

import com.tanhua.model.domian.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {
    public void save(UserInfo userInfo);

    void update(UserInfo userInfo);

    UserInfo findById(Long id);
    /**
     * 批量查询用户详情
     *      返回值:Map<id,UserInfo>
     */
    Map<Long,UserInfo> findByIds(List<Long> userIds,UserInfo info);
}