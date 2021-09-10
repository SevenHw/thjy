package com.tanhua.dubbo.api;

import java.util.Map;

public interface UserLikeApi {
    /**
     * 保存或这更新
     *
     * @param userLike
     */
    Boolean save(Long userId, Long userLike, Boolean isLike);

    /**
     * 查询粉丝,喜欢,相互喜欢
     *
     * @param userId
     * @return
     */
    Map loveUniversal(Long userId);
}
