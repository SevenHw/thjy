package com.tanhua.dubbo.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IdWorker idWorker;

    /**
     * 上传视频
     *
     * @param video
     */
    @Override
    public void save(Video video) {
        video.setVid(idWorker.getNextId("video"));
        mongoTemplate.save(video);
    }

    /**
     * 根据vid分页查询视频
     *
     * @param list     vid集合
     * @param page     当前页
     * @param pagesize 查询多少条
     * @return
     */
    @Override
    public PageResult findVid(List<Long> list, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("vid").in(list);
        Query query = Query.query(criteria).skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        return new PageResult(page, pagesize, 0L, videos);
    }

    /**
     * 跟也查询视频
     *
     * @param page     当前页
     * @param pagesize 查询多少条
     * @return
     */
    @Override
    public PageResult findAll(Integer page, Integer pagesize) {
        //创建统计对象，设置统计参数
        /*TypedAggregation aggregation = Aggregation.newAggregation(Video.class, Aggregation.sample(pagesize));
        //调用mongoTemplate方法统计
        AggregationResults<Video> results = mongoTemplate.aggregate(aggregation, Video.class);
        List<Video> videos = results.getMappedResults();
        return new PageResult(page, pagesize, 0L, videos);*/
        //查询总数
        long count = mongoTemplate.count(new Query(), Video.class);
        //分页查询数据列表
        Query query = new Query().limit(pagesize).skip((page - 1) * pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        List<Video> list = mongoTemplate.find(query, Video.class);
        //构建返回
        return new PageResult(page, pagesize, count, list);
    }

    /**
     * 添加关注
     *
     * @param focusUser
     */
    @Override
    public void saveFollowUser(FocusUser focusUser) {
        mongoTemplate.save(focusUser);
    }

    /**
     * 取消关注
     *
     * @param userId
     * @param friendId
     * @return
     */
    @Override
    public Boolean findByUserIdBool(Long userId, Long friendId) {
        Criteria criteria = Criteria.where("userId").is(userId).and("followUserId").is(friendId);
        Query query = Query.query(criteria);
        FocusUser focusUser = mongoTemplate.findOne(query, FocusUser.class);
        return ObjectUtil.isEmpty(focusUser);
    }

    /**
     * 根据id删除
     *
     * @param userId
     * @param friendId
     */
    @Override
    public void delete(Long userId, Long friendId) {
        Criteria criteria = Criteria.where("userId").is(userId).and("followUserId").is(friendId);
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, FocusUser.class);
    }

    /**
     * 根据id查询
     *
     * @param videoId
     * @return
     */
    @Override
    public Video findById(String videoId) {
        Criteria criteria = Criteria.where("id").is(videoId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Video.class);
    }
}