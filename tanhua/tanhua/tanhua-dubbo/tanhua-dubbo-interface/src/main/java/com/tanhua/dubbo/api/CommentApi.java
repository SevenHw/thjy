package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;

public interface CommentApi {
    Integer save(Comment comment);

    /**
     * 分页查询评论
     *
     * @param movementId
     * @param comment
     * @param page
     * @param pagesize
     * @return
     */
    List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize);

    /**
     * 动态点赞
     *
     * @param movementId
     * @param userId
     * @param like
     * @return
     */
    Boolean hasComment(String movementId, Long userId, CommentType like);

    Integer delete(Comment comment);

    //根据推荐id查询
    Integer find(String movementId,Long userId);
}
