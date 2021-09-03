package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TimeLineService timeLineService;

    /**
     * 发布动态
     *
     * @param movement
     */
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

    /**
     * 查看自己动态
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).skip((long) (page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, 0L, movements);
    }


}