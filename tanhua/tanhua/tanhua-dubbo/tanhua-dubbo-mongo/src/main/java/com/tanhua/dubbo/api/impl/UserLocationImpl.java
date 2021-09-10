package com.tanhua.dubbo.api.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.geo.Circle;

import java.util.List;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-10 11:00
 **/
@DubboService
public class UserLocationImpl implements UserLocationApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 更新地理位置
     *
     * @param userId
     * @param latitude
     * @param longitude
     * @param addrStr
     * @return
     */
    @Override
    public Boolean updateLocation(Long userId, Double latitude, Double longitude, String addrStr) {
        try {
            Criteria criteria = Criteria.where("userId").is(userId);
            Query query = Query.query(criteria);
            //通过userid查询数据
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
            //判断是否存在
            if (ObjectUtil.isEmpty(userLocation)) {
                userLocation = new UserLocation();
                userLocation.setUserId(userId);
                userLocation.setLocation(new GeoJsonPoint(latitude, longitude));
                userLocation.setAddress(addrStr);
                userLocation.setCreated(System.currentTimeMillis());
                userLocation.setUpdated(System.currentTimeMillis());
                userLocation.setLastUpdated(System.currentTimeMillis());
                mongoTemplate.save(userLocation);
            } else {
                GeoJsonPoint point = new GeoJsonPoint(latitude, longitude);
                Long updated = userLocation.getUpdated();
                Update update = Update.update("location", point)
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", updated)
                        .set("address", addrStr);
                mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 搜附近
     *
     * @param userId
     * @param metre
     * @return
     */
    @Override
    public List<Long> findByUser(Long userId, Double metre) {
        //1、根据用户id，查询用户的位置信息
        Query query = Query.query(Criteria.where("userId").is(userId));
        UserLocation location = mongoTemplate.findOne(query, UserLocation.class);
        if (location == null) {
            return null;
        }
        //2、已当前用户位置绘制原点
        GeoJsonPoint point = location.getLocation();
        //3、绘制半径
        Distance distance = new Distance(metre / 1000, Metrics.KILOMETERS);
        //4、绘制圆形
        Circle circle = new Circle(point, distance);
        //5、查询
        Query locationQuery = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> list = mongoTemplate.find(locationQuery, UserLocation.class);
        return CollUtil.getFieldValues(list, "userId", Long.class);
    }
}
