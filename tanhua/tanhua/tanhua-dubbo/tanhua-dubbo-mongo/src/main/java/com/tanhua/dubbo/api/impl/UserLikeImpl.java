package com.tanhua.dubbo.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
}
