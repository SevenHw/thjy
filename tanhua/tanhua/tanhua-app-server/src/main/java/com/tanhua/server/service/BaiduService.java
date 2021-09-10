package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-10 11:04
 **/
@Service
public class BaiduService {
    @DubboReference
    private UserLocationApi userLocationApi;

    /**
     * 上报地理位置
     *
     * @param latitude
     * @param longitude
     * @param addrStr
     */
    public void location(Double latitude, Double longitude, String addrStr) {
        //更新地理位置
        Boolean flag = userLocationApi.updateLocation(UserHolder.getUserId(), latitude, longitude, addrStr);
        //上传或者更新失败就报异常
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }
}
