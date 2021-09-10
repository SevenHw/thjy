package com.tanhua.dubbo.api;

import java.util.List;

public interface UserLocationApi {


    /**
     * 跟新地址
     *
     * @param userId
     * @param latitude
     * @param longitude
     * @param addrStr
     * @return
     */
    Boolean updateLocation(Long userId, Double latitude, Double longitude, String addrStr);

    List<Long>  findByUser(Long userId, Double valueOf);
}
