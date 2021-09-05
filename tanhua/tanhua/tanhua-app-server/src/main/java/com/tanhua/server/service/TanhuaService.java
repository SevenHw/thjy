package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.Interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TanhuaService {
    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 今日佳人
     *
     * @return
     */
    public TodayBest todayBest() {
        //获取用户id
        Long userId = UserHolder.getUserId();
        //调用API查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }
        //将RecommendUser转化为TodayBest
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        return vo;
    }

   /* public PageResult recommendation(RecommendUserDto dto) {
        //1.获取用户id
        Long userId = UserHolder.getUserId();
        //2.调用recommendUserApi分页查询数据列表
        PageResult pr = recommendUserApi.queryRecommendUserList(dto.getPage(), dto.getPagesize(), userId);
        //3.获取分页中的RecommendUser数据列表
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        //4.判断列表是否为空
        if (items == null) {
            return pr;
        }
        //5.循环RecommendUser数据列表,根据推荐的用户id查询用户详情
        ArrayList<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            Long recommendUserId = item.getUserId();
            UserInfo userInfo = userInfoApi.findById(recommendUserId);
            if (userInfo != null) {
                //条件判断
                if (!StringUtils.isEmpty(dto.getGender()) && !dto.getGender().equals(userInfo.getGender())) {
                    continue;
                }
                if (dto.getAge() != null && dto.getAge() < userInfo.getAge()) {
                    continue;
                }
                TodayBest vo = TodayBest.init(userInfo, item);
                list.add(vo);
            }
        }
        //6.构造返回值
        pr.setItems(list);
        return pr;
    }*/

    /**
     * 分页查询推荐列表
     *
     * @param dto
     * @return
     */
    public PageResult recommendation(RecommendUserDto dto) {
        //1.获取用户id
        Long userId = UserHolder.getUserId();
        //2.调用recommendUserApi分页查询数据列表
        PageResult pr = recommendUserApi.queryRecommendUserList(dto.getPage(), dto.getPagesize(), userId);
        //3.获取分页中的RecommendUser数据列表
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        //4.判断列表是否为空
        if (CollUtil.isEmpty(items)) {
            return pr;
        }
        //5.提取所有的推荐的用户id列表
        List<Long> ids = CollUtil.getFieldValues(items, "userId", Long.class);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());
        //6.构建查询条件,查询虽有用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);
        //7.循环推荐的数据列表,构建vo对象
        ArrayList<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            UserInfo info = map.get(item.getUserId());
            if (info != null) {
                TodayBest vo = TodayBest.init(info, item);
                list.add(vo);
            }
        }
        //8.构造返回值
        pr.setItems(list);
        return pr;
    }
}
