package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;

import java.util.List;

public interface VisitorsApi {


    /**
     * 根据时间和当前操作用户id查询信息
     *
     * @param date
     * @param userId
     * @return
     */
    List<Visitors> findByUsers(Long date, Long userId);

    /**
     * 保存用户数据
     *
     * @param visitors
     */
    Boolean save(Visitors visitors);
}
