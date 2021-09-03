package com.tanhua.dubbo.api;


import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

public interface MovementApi {

    /**
     * 发布动态
     * @param movement
     */
    void publish(Movement movement);

    PageResult findByUserId(Long userId, Integer page, Integer pagesize);
}