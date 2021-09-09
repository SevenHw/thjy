package com.tanhua.dubbo.api;

public interface UserLikeApi {
    /**
     * 保存或这更新
     *
     * @param userLike
     */
    Boolean save(Long userId, Long userLike ,Boolean isLike);
}
