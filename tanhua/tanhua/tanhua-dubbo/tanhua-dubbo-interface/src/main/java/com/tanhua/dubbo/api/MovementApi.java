package com.tanhua.dubbo.api;


import com.tanhua.model.mongo.Movement;

public interface MovementApi {

    /**
     * 发布动态
     * @param movement
     */
    void publish(Movement movement);
}