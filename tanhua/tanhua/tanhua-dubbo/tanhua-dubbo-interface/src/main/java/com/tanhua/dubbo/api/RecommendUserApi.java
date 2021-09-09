package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {

    RecommendUser queryWithMaxScore(Long toUserId);


    /**
     * 分页查询
     */
    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);

    /**
     * 查看佳人信息
     *
     * @param userId
     * @param userId1
     * @return
     */
    RecommendUser queryByUserId(Long userId, Long userId1);

    /**
     *
     * @param userId
     * @param index
     * @return
     */
    List<RecommendUser> findByUserId(Long userId, Integer index);


}