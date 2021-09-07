package com.tanhua.dubbo.api;

import com.tanhua.model.domian.User;

public interface UserApi {
    /**
     * 根据手机号查询
     *
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 保存用户返回id
     *
     * @param user
     * @return
     */
    Long save(User user);


    /**
     * 更新手机号码
     *
     * @param phone
     * @param userId
     */
    void update(String phone, Long userId);


    User findById(Long userId);

    /**
     * 更新
     *
     * @param user
     */
    void hxUpdate(User user);

    /**
     * 通过用户id查询
     * @param userId
     * @return
     */
    User findHx(Long userId);

    /**
     * 根据环信id查询用户
     * @param huanxinId
     * @return
     */
    User findByHxId(String huanxinId);
}
