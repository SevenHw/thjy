package com.tanhua.dubbo.utils;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

@Async
public class TimeLineService {
    private MongoTemplate mongoTemplate;

    public void saveTimeLine(Movement movement) {
        //2.查询当前用户的数据
        Criteria criteria = Criteria.where("userId").is(movement.getUserId());
        Query query = new Query(criteria);
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        //3.循环好友数据,构造时间先数据存入数据库
        for (Friend friend : friends) {
            MovementTimeLine movementTimeLine = new MovementTimeLine();
            movementTimeLine.setMovementId(movement.getId());
            movementTimeLine.setUserId(friend.getUserId());//用户id
            movementTimeLine.setFriendId(friend.getFriendId());//好友id
            movementTimeLine.setFriendId(System.currentTimeMillis());
            mongoTemplate.save(movementTimeLine);
        }
    }
}
