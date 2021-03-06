package com.tanhua.dubbo.api.impl;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-05 16:21
 **/
@DubboService
public class CommentAPIImpl implements CommentApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 发布动态
     *
     * @param comment
     * @return
     */
    @Override
    public Integer save(Comment comment) {
        if (comment.getCommentType() != CommentType.COMMENT.getType()) {
            //1、查询动态
            Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
            //2、向comment对象设置被评论人属性
            if (ObjectUtil.isNull(movement)) {
                comment.setPublishUserId(movement.getUserId());
            }
            //3、保存到数据库
            mongoTemplate.save(comment);
            //4、更新动态表中的对应字段
            Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
            Update update = new Update();
            if (comment.getCommentType() == CommentType.LIKE.getType()) {
                update.inc("likeCount", 1);
            } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
                update.inc("commentCount", 1);
            } else {
                update.inc("loveCount", 1);
            }
            //设置更新参数
            FindAndModifyOptions options = new FindAndModifyOptions();
            //获取更新后的最新数据
            options.returnNew(true);
            Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
            //5、获取最新的评论数量，并返回
            return modify.statisCount(comment.getCommentType());
        } else {
            //查询评论发布人id
            Movement commentById = mongoTemplate.findById(comment.getPublishId(), Movement.class);
            //评论发布人的id
            Long userId = commentById.getUserId();
            comment.setPublishUserId(userId);
            //3、保存到数据库
            mongoTemplate.save(comment);
            //更新表中数据
            Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
            Update update = new Update();
            update.inc("likeCount", 1);
            //设置更新参数
            FindAndModifyOptions options = new FindAndModifyOptions();
            //获取更新后的最新数据
            options.returnNew(true);
            Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
            //5、获取最新的评论数量，并返回
            return modify.statisCount(CommentType.COMMENT.getType());
        }

    }

    /**
     * 分页查询评论
     *
     * @param movementId
     * @param comment
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize) {
        //构建查询条件
        Criteria criteria = Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(comment.getType());
        Query query = Query.query(criteria)
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Comment.class);
    }

    /**
     * 判断comment数据是否存在
     *
     * @param movementId
     * @param userId
     * @param commentType
     * @return
     */
    public Boolean hasComment(String movementId, Long userId, CommentType commentType) {
        Criteria criteria = Criteria
                .where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(commentType.getType());
        Query query = Query.query(criteria);
        //判断数据是否存在
        return mongoTemplate.exists(query, Comment.class);
    }

    /**
     * 删除点赞_喜欢
     *
     * @param comment
     * @return
     */
    @Override
    public Integer delete(Comment comment) {
        //1、删除Comment表数据
        Criteria criteria = Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        //2、修改动态表中的总数量
        Query movementQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else {
            update.inc("loveCount", -1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);
        //5、获取最新的评论数量，并返回
        return modify.statisCount(comment.getCommentType());


    }

    /**
     * 取消评论点赞
     *
     * @param movementId
     * @param userId
     * @return
     */
    @Override
    public Integer find(String movementId, Long userId) {
        //删除点赞数据
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.LIKE.getType());
        Query queryDelete = Query.query(criteria);
        mongoTemplate.remove(queryDelete, Comment.class);
        //修改表
        Query query = Query.query(Criteria.where("id").is(movementId));
        Update update = new Update();
        update.inc("likeCount", -1);
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Comment modify = mongoTemplate.findAndModify(query, update, options, Comment.class);
        //5、获取最新的评论数量，并返回
        return modify.statisCount();
    }

    /**
     * 查询点赞
     *
     * @param like
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<Comment> findByUserId(CommentType like, Long userId, Integer page, Integer pagesize) {
        //点赞
        if (like.getType() == CommentType.LIKE.getType()) {
            //构建查询条件
            Criteria criteria = Criteria.where("publishUserId").is(userId)
                    .and("commentType").is(CommentType.LIKE.getType());
            Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize);
            //构造返回值
            return mongoTemplate.find(query, Comment.class);
        } else if (like.getType() == CommentType.COMMENT.getType()) {
            //构建查询条件
            Criteria criteria = Criteria.where("publishUserId").is(userId)
                    .and("commentType").is(CommentType.COMMENT.getType());
            Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize);
            //构造返回值
            return mongoTemplate.find(query, Comment.class);
        } else {
            //构建查询条件
            Criteria criteria = Criteria.where("publishUserId").is(userId)
                    .and("commentType").is(CommentType.LOVE.getType());
            Query query = Query.query(criteria).skip((page - 1) * pagesize).limit(pagesize);
            //构造返回值
            return mongoTemplate.find(query, Comment.class);
        }
    }

    /**
     * 视频评论
     *
     * @param comment
     */
    @Override
    public void saveVideo(Comment comment) {
        mongoTemplate.save(comment);
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == 2) {
            update.inc("commentCount", 1);
        } else if (comment.getCommentType() == 1) {
            update.inc("likeCount", 1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        //获取更新后的最新数据
        options.returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Video.class);
    }

    /**
     * 评论点赞
     */
    @Override
    public void saveVideoComments(Comment comment) {
        mongoTemplate.save(comment);
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc("likeCount", 1);
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        //获取更新后的最新数据
        options.returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Comment.class);
    }

    /**
     * 取消视频评论点赞
     *
     * @param comment
     */
    @Override
    public void deleteComments(Comment comment) {
        //删除点赞数据
        Criteria criteria = Criteria.where("userId").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(CommentType.LIKE.getType());
        Query queryDelete = Query.query(criteria);
        mongoTemplate.remove(queryDelete, Comment.class);
        //修改表
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        update.inc("likeCount", -1);
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);//获取更新后的最新数据
        Comment modify = mongoTemplate.findAndModify(query, update, options, Comment.class);
    }

    /**
     * 取消
     *
     * @param comment
     */
    @Override
    public void DisVideo(Comment comment) {
        Criteria criteria = Criteria.where("id").is(comment.getPublishId()).and("userId").is(comment.getUserId()).and("publishUserId").is(comment.getPublishUserId());
        Query query1 = Query.query(criteria);
        mongoTemplate.remove(query1, Comment.class);

        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        Update update = new Update();
        if (comment.getCommentType() == 2) {
            update.inc("commentCount", -1);
        } else if (comment.getCommentType() == 1) {
            update.inc("likeCount", -1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        //获取更新后的最新数据
        options.returnNew(true);
        mongoTemplate.findAndModify(query, update, options, Video.class);
    }

    /**
     * 更根据id查询
     *
     * @param videoId
     * @return
     */
    @Override
    public Comment find(String videoId) {
        Criteria criteria = Criteria.where("id").is(new ObjectId(videoId));
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Comment.class);
    }
}
