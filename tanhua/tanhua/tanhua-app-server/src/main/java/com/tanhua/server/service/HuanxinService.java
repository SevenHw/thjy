package com.tanhua.server.service;

import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domian.User;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.Interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-07 09:43
 **/
@Service
public class HuanxinService {
    @DubboReference
    private UserApi userApi;

    /**
     * 环信用户登录
     *
     * @return
     */
    public HuanXinUserVo userLogin() {
        //获取当前操作用户id
        Long userId = UserHolder.getUserId();
        //调用api查询通过id查询出用户
        User user = userApi.findHx(userId);
        if (ObjectUtil.isNull(user)) {
            return null;
        }
        return new HuanXinUserVo(user.getHxUser(), user.getHxPassword());
    }
}
