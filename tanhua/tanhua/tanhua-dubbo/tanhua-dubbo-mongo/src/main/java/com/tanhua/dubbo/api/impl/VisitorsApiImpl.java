package com.tanhua.dubbo.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.VisitorsApi;
import com.tanhua.model.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@DubboService
public class VisitorsApiImpl implements VisitorsApi {


    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存用户信息
     * 同一个客户一天只记录一次
     */
    @Override
    public Boolean save(Visitors visitors) {
        try {
            Criteria criteria = Criteria.where("userId").is(visitors.getUserId())
                    .and("visitorUserId").is(visitors.getVisitorUserId());
            Query query = Query.query(criteria);
            //如果数据不存在保存
            if (!mongoTemplate.exists(query, Visitors.class)) {
                mongoTemplate.save(visitors);
            } else {
                //存在就更新时间
                Update update = Update.update("date", visitors.getDate());
                mongoTemplate.updateFirst(query, update, Visitors.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据用户id和访问时间查询访问信息
     *
     * @param date
     * @param userId
     * @return
     */
    @Override
    public List<Visitors> findByUsers(Long date, Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        if (!ObjectUtil.isNull(date)) {
            criteria.and("date").gt(date);
        }
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));
        return mongoTemplate.find(query, Visitors.class);
    }
}
