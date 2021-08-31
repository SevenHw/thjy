package com.tanhua.dubbo.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domian.UserInfo;

public interface BlackListApi {
    //分页查询黑名单列表
    IPage<UserInfo> findByUserId(Long userId, int page, int size);

    //移除黑名单
    void delete(Long userId, Long blackUserId);
}
