package com.tanhua.dubbo.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-09 22:55
 **/
@DubboService
public class UserLikeImpl implements UserLikeApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean save(Long userId, Long likeUserId, Boolean isLike) {
        try {
            Criteria criteria = Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId);
            Query query = Query.query(criteria);
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            //如果不存在保存
            if (ObjectUtil.isEmpty(userLike)) {
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            } else {
                //3、更新
                Update update = Update.update("isLike", isLike)
                        .set("updated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map loveUniversal(Long userId) {
        HashMap<String, Integer> map = new HashMap<>();
        //查询喜欢
        Criteria criteriaLoveCount = Criteria.where("userId").is(userId);
        Query queryLoveCount = Query.query(criteriaLoveCount);
        //查询出所有的用户信息
        List<UserLike> userLikesLove = mongoTemplate.find(queryLoveCount, UserLike.class);
        //抽取userId字段
        List<Long> userIds = CollUtil.getFieldValues(userLikesLove, "userId", Long.class);
        if (ObjectUtil.isEmpty(userLikesLove)) {
            map.put("loveCount", 0);
        } else {
            map.put("loveCount", userLikesLove.size() + 1);
        }
        //查询粉丝
        Criteria criteriaFanCount = Criteria.where("likeUserId").is(userId);
        Query queryFanCount = Query.query(criteriaFanCount);
        //查询出所有的用户信息
        List<UserLike> userLikesFanCount = mongoTemplate.find(queryLoveCount, UserLike.class);
        //抽取userId字段
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikesLove, "likeUserId", Long.class);
        if (ObjectUtil.isEmpty(userLikesFanCount)) {
            map.put("fanCount", 0);
        } else {
            map.put("fanCount", userLikesFanCount.size() + 1);
        }
        //查询相互喜欢
        //工具类取交集
        Collection<Long> eachLoveCount = CollectionUtil.intersection(userIds, likeUserIds);
        if (ObjectUtil.isEmpty(eachLoveCount)) {
            map.put("eachLoveCount", 0);
        } else {
            map.put("eachLoveCount", eachLoveCount.size() + 1);
        }
        return map;
    }
}
