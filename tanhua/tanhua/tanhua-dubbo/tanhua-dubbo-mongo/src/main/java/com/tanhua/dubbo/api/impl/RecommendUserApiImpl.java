package com.tanhua.dubbo.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //查询今日佳人
    public RecommendUser queryWithMaxScore(Long toUserId) {

        //根据toUserId查询，根据评分score排序，获取第一条
        //构建Criteria
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //构建Query对象
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score")))
                .limit(1);
        //调用mongoTemplate查询

        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pagesize
     * @param toUserId
     * @return
     */
    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        //1.构造Criteria对象
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //2.构造query对象
        Query query = Query.query(criteria);
        //3.查询总数
        long count = mongoTemplate.count(query, RecommendUser.class);
        //4.查询数据列表
        query.with(Sort.by(Sort.Order.desc("score"))).skip((page - 1) * pagesize).limit(pagesize);
        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        //5.构造返回值
        return new PageResult(page, pagesize, count, list);
    }

    /**
     * 查询佳人信息
     *
     * @param userId
     * @param toUserId
     * @return
     */
    @Override
    public RecommendUser queryByUserId(Long userId, Long toUserId) {
        //构造查询条件
        Criteria criteria = Criteria.where("userId").is(userId).and("toUserId").is(toUserId);
        Query query = Query.query(criteria);
        //构造返回值
        RecommendUser user = mongoTemplate.findOne(query, RecommendUser.class);
        if (ObjectUtil.isEmpty(user)) {
            user = new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(toUserId);
            //构建缘分值
            user.setScore(95d);
        }
        return user;
    }

    /**
     * 查询不喜欢和喜欢的数据
     *
     * @param userId
     * @param index
     * @return
     */
    @Override
    public List<RecommendUser> findByUserId(Long userId, Integer index) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        //查询出所有的不喜欢
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);
        //获取其中所有的toUserId
        List<Long> toUserId = CollUtil.getFieldValues(userLikes, "likeUserId", Long.class);
        //2、构造查询推荐用户的条件
        Criteria criteriaRecommend = Criteria.where("toUserId").is(userId).and("userId").nin(userLikes);
        //3、使用统计函数，随机获取推荐的用户列表
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(RecommendUser.class,
                Aggregation.match(criteriaRecommend),//指定查询条件
                Aggregation.sample(index)//查询数量
        );
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(newAggregation, RecommendUser.class);
        //4、构造返回
        return results.getMappedResults();
    }


}