package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;

import java.util.List;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-08 16:51
 **/

public interface FriendApi {
    /**
     * 添加好友信息
     *
     * @param friendId
     * @param userId
     */
    void save(Long friendId, Long userId);

    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    List<Friend> findByUserId(Long userId, Integer page, Integer pagesize);
}
