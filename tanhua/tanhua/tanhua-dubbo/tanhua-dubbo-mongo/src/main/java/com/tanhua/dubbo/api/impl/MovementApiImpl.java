package com.tanhua.dubbo.api.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
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

    /**
     * 根据用户id查看用户好友动态
     *
     * @param page     当前页
     * @param pagesize 一页显示几条
     * @param friendId 当前用户操作id
     * @return
     */
    @Override
    public List<Movement> findFriendMovement(Integer page, Integer pagesize, Long friendId) {
        //1.根据friendId查询时间线表
        Criteria criteria = Criteria.where("friendId").is(friendId);
        Query queryTimeLines = Query.query(criteria);
        List<MovementTimeLine> timeLines = mongoTemplate.find(queryTimeLines, MovementTimeLine.class);
        //2.提取动态id列表
        List<ObjectId> list = CollUtil.getFieldValues(timeLines, "movementId", ObjectId.class);
        //3.根据动态id查询动态详情
        Criteria movementQuery = Criteria.where("id").is(list);
        Query queryMovement = Query.query(movementQuery);
        //4.返回数据
        return mongoTemplate.find(queryMovement, Movement.class);
    }

    /**
     * 随机获得动态
     *
     * @param pagesize
     */
    @Override
    public List<Movement> randomMovements(Integer pagesize) {
        //1、创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(pagesize));
        //2、调用mongoTemplate方法统计
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        //3、获取统计结果
        return results.getMappedResults();
    }

    /**
     * 根据pid查询
     *
     * @param pids
     * @return
     */
    @Override
    public List<Movement> findMovementsByPids(List<Long> pids) {
        Criteria criteria = Criteria.where("pid").in(pids);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Movement.class);
    }

    /**
     * 根据id查询动态
     *
     * @param movementId
     * @return
     */
    @Override
    public Movement findById(String movementId) {
        return mongoTemplate.findById(movementId, Movement.class);
    }

    @Override
    public PageResult findByUserId(Long userId, Integer state, Integer page, Integer pagesize) {
        //创建分页查询条件
        Query query = new Query().skip((page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //当userId不等于null时
        if (!ObjectUtil.isEmpty(userId)) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        if (!ObjectUtil.isEmpty(state)) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        List<Movement> list = mongoTemplate.find(query, Movement.class);
        long count = mongoTemplate.count(query, Movement.class);
        return new PageResult(page, pagesize, count, list);
    }

    /**
     * 评论列表翻页
     * /manage/messages/comments
     *
     * @param page
     * @param pagesize
     * @param sortProp  排序字段
     * @param sortOrder 升序降序
     * @param publishId 动态id
     * @return
     */
    @Override
    public List<Comment> findByPublidshId(Integer page, Integer pagesize, String sortProp, String sortOrder, String publishId) {
        Criteria criteria = Criteria.where("publishId").is(new ObjectId(publishId))
                .and("commentType").is(CommentType.COMMENT.getType());
        Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize);
        if (sortOrder.equals("ascending ")) {
            query.with(Sort.by(Sort.Order.asc(sortProp)));
        } else {
            query.with(Sort.by(Sort.Order.desc(sortProp)));
        }
        return mongoTemplate.find(query, Comment.class);
    }
}