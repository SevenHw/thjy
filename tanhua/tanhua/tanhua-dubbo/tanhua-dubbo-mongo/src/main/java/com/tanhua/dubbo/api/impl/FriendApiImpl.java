package com.tanhua.dubbo.api.impl;

import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.model.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-08 16:52
 **/
@DubboService
public class FriendApiImpl implements FriendApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long friendId, Long userId) {
        //判断是否添加好友
        Criteria criteria1 = Criteria.where("userId").is(userId).and("friendId").is(friendId);
        Query query1 = Query.query(criteria1);
        if (!mongoTemplate.exists(query1, Friend.class)) {
            //如果不存在则添加数据
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            mongoTemplate.save(friend);
        }
        Criteria criteria2 = Criteria.where("friendId").is(userId).and("userId").is(friendId);
        Query query2 = Query.query(criteria1);
        if (!mongoTemplate.exists(query2, Friend.class)) {
            //如果不存在则添加数据
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            mongoTemplate.save(friend);
        }
    }

    @Override
    public List<Friend> findByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize);
        return mongoTemplate.find(query, Friend.class);
    }
}
