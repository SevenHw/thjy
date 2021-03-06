package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VisitorsApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: tanhua
 * @author: seven
 * @create: 2021-09-05 15:42
 **/
@Service
@Slf4j
public class CommentsService {
    @DubboReference
    private CommentApi commentApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @DubboReference
    private MovementApi movementApi;
    @DubboReference
    private VisitorsApi visitorsApi;


    /**
     * 发布动态
     *
     * @param movementId
     * @param comment
     */
    public void comments(String movementId, String comment) {
        //1、获取操作用户id
        Long userId = UserHolder.getUserId();
        //2、构造Comment
        Movement movement = movementApi.findById(movementId);
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setContent(comment);
        comment1.setUserId(userId);
        comment1.setPublishUserId(movement.getUserId());
        comment1.setCreated(System.currentTimeMillis());
        //3、调用API保存评论
        Integer commentCount = commentApi.save(comment1);
        log.info("commentCount = " + commentCount);
    }


    /**
     * 分页查询评论列表
     *
     * @param page       当前页数
     * @param pagesize   页尺寸
     * @param movementId 动态id
     * @return
     */
    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        //1、调用API查询评论列表
        List<Comment> list = commentApi.findComments(movementId, CommentType.COMMENT, page, pagesize);
        //2、判断list集合是否存在
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //3、提取所有的用户id,调用UserInfoAPI查询用户详情
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //4、构造vo对象
        List<CommentVo> vos = new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (!ObjectUtil.isEmpty(userInfo)) {
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        }
        //5、构造返回值
        return new PageResult(page, pagesize, 0L, vos);
    }

    /**
     * 动态点赞
     *
     * @param movementId 动态id
     * @return
     */
    public Integer like(String movementId) {
        //查询用户是否点赞
        CommentType like = CommentType.LIKE;
        String hashkey = Constants.MOVEMENT_LIKE_HASHKEY;
        Integer count = already(movementId, like, hashkey);
        return count;
    }

    /**
     * 喜欢
     *
     * @param movementId
     * @return
     */
    public Integer love(String movementId) {
        //查询用户是否点赞
        CommentType like = CommentType.LOVE;
        String hashkey = Constants.MOVEMENT_LOVE_HASHKEY;
        Integer count = already(movementId, like, hashkey);
        return count;
    }


    /**
     * 取消点赞
     * /movements/:id/dislike
     *
     * @param movementId
     * @return
     */
    public Integer dislike(String movementId) {
        //1、调用API查询用户是否已点赞
        //查询用户是否点赞
        CommentType like = CommentType.LIKE;
        String hashkey = Constants.MOVEMENT_LIKE_HASHKEY;
        Integer count = period(movementId, like, hashkey);
        return count;
    }

    /**
     * 取消喜欢
     *
     * @param movementId
     * @return
     */
    public Integer unlove(String movementId) {
        CommentType like = CommentType.LOVE;
        String hashkey = Constants.MOVEMENT_LOVE_HASHKEY;
        Integer count = period(movementId, like, hashkey);
        return count;
    }

    /**
     * 评论取消点赞
     *
     * @param movementId
     * @return
     */
    public Integer commentDislike(String movementId) {
        CommentType like = CommentType.LIKE;
        String hashkey = Constants.MOVEMENT_COMMENT_HASHKEY;
        //查看是否点赞
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), like);
        //为点赞,抛出异常
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //通过评论id查出动态id
        Long userId = UserHolder.getUserId();
        Integer count = commentApi.find(movementId, userId);
        //4、拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = hashkey + UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key, hashKey, "1");
        return count;
    }

    /**
     * 评论点赞
     *
     * @param movementId
     * @return
     */
    public Integer commentLike(String movementId) {
        CommentType like = CommentType.LIKE;
        String hashkey = Constants.MOVEMENT_COMMENT_HASHKEY;
        //查看是否点赞
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), like);
        //如果已经点赞喜欢,抛出异常
        if (hasComment) {
            throw new BusinessException(ErrorResult.likeError());
        }
        Movement movement = movementApi.findById(movementId);
        //通过评论id查出动态id
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(like.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        comment.setPublishUserId(movement.getUserId());
        Integer count = commentApi.save(comment);
        //4、拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = hashkey + UserHolder.getUserId();
        redisTemplate.opsForHash().put(key, hashKey, "1");
        return count;
    }


    /**
     * 已经点赞喜欢
     *
     * @param movementId 动态id
     * @param like       操作类型
     * @param hashkey
     * @return
     */
    private Integer already(String movementId, CommentType like, String hashkey) {
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), like);
        //如果已经点赞喜欢,抛出异常
        if (hasComment) {
            throw new BusinessException(ErrorResult.loveError());
        }
        Movement movement = movementApi.findById(movementId);
        //调用api保存数据到mongodb
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(like.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        comment.setPublishUserId(movement.getUserId());
        Integer count = commentApi.save(comment);
        //4、拼接redis的key，将用户的点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = hashkey + UserHolder.getUserId();
        redisTemplate.opsForHash().put(key, hashKey, "1");
        return count;
    }

    /**
     * 取消喜欢点赞
     *
     * @param movementId 动态id
     * @param like       操作类型
     * @param hashkey
     * @return
     */
    private Integer period(String movementId, CommentType like, String hashkey) {
        Boolean hasComment = commentApi.hasComment(movementId, UserHolder.getUserId(), like);
        //2、如果未点赞，抛出异常
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disloveError());
        }
        //3、调用API，删除数据，返回点赞数量
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(like.getType());
        comment.setUserId(UserHolder.getUserId());
        Integer count = commentApi.delete(comment);
        //4、拼接redis的key，删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = hashkey + UserHolder.getUserId();
        redisTemplate.opsForHash().delete(key, hashKey);
        return count;
    }


    /**
     * 谁看过我
     *
     * @return
     */
    public List<VisitorsVo> visitors() {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //当前时间
        long time = System.currentTimeMillis();
        //查询访问时间
        String key = Constants.VISITORS_USER;
        String hashKey = String.valueOf(userId);
        String value = (String) redisTemplate.opsForHash().get(key, hashKey);
        Long date = StringUtils.isEmpty(value) ? null : Long.valueOf(value);
        //调用api查询用户
        List<Visitors> list = visitorsApi.findByUsers(date, userId);
        //判断是否为空
        if (ObjectUtil.isEmpty(list)) {
            return new ArrayList<VisitorsVo>();
        }
        //获得来访用户id
        List<Long> visitorUserIds = CollUtil.getFieldValues(list, "visitorUserId", Long.class);
        //查询来访用户信息
        //得到来访用户id信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(visitorUserIds, null);
        List<VisitorsVo> vos = new ArrayList<>();
        for (Visitors visitors : list) {
            UserInfo info = map.get(visitors.getVisitorUserId());
            if (!ObjectUtil.isEmpty(info)) {
                VisitorsVo vo = VisitorsVo.init(info, visitors);
                vos.add(vo);
            }
        }
        return vos;
    }
}
