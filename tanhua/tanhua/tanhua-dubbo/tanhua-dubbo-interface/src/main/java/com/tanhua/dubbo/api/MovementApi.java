package com.tanhua.dubbo.api;


import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface MovementApi {

    /**
     * 发布动态
     *
     * @param movement
     */
    void publish(Movement movement);

    /**
     * 根据用户id查看用户动态
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findByUserId(Long userId, Integer page, Integer pagesize);

    /**
     * 根据用户id查看用户好友动态
     *
     * @param page     当前页
     * @param pagesize 一页显示几条
     * @param friendId 用户id
     * @return
     */
    List<Movement> findFriendMovement(Integer page, Integer pagesize, Long friendId);

    /**
     * 随机获得动态
     *
     * @param pagesize
     */
    List<Movement> randomMovements(Integer pagesize);

    /**
     * 随机获取多条动态
     *
     * @param pids
     * @return
     */
    List<Movement> findMovementsByPids(List<Long> pids);

    /**
     * 根据id查询动态
     *
     * @param movementId
     * @return
     */
    Movement findById(String movementId);

    /**
     * 分页查询个人用户视频信息
     *
     * @param userId
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findByUserId(Long userId, Integer state, Integer page, Integer pagesize);

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
    List<Comment> findByPublidshId(Integer page, Integer pagesize, String sortProp, String sortOrder, String publishId);
}