package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

@DubboService
public class MovementApiImpl implements MovementApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TimeLineService timeLineService;

    @Override
    public void publish(Movement movement) {
        try {
            //1.保存动态详情
            //设置PID
            movement.setPid(idWorker.getNextId("movement"));
            //设置时间
            movement.setCreated(System.currentTimeMillis());
            mongoTemplate.save(movement);
            timeLineService.saveTimeLine(movement);
        } catch (Exception e) {
            //此处省略事务回滚
            e.printStackTrace();
        }
    }


}