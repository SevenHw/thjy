package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domian.User;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-07 16:02
 **/
@Service
public class MessagesService {
    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 根据环信的用户id查询用户详情
     *
     * @param huanxinId
     * @return
     */
    public UserInfoVo findByHxId(String huanxinId) {
        //根据环信id查询用户id
        User user = userApi.findByHxId(huanxinId);
        //根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId());
        UserInfoVo vo = new UserInfoVo();
        //copy同名同类型的属性
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }
}
