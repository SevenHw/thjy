package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domian.Question;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.NearUserVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TanhuaService {
    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserLikeApi userLikeApi;

    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MessagesService messagesService;

    @Autowired
    private UserLocationApi userLocationApi;

    @DubboReference
    private VisitorsApi visitorsApi;

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

    /**
     * 分页查询推荐列表
     * <p>
     * CollUtil.isEmpty()判断集合是否为空    当集合长度为0是返回true
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
        if (ObjectUtil.isEmpty(items)) {
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

    /**
     * 查询佳人信息
     *
     * @param userId
     * @return
     */
    public TodayBest personalInfo(Long userId) {
        //根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(userId);
        //根据操作人id和查看的用户id,查询两者的推荐数据
        RecommendUser user = recommendUserApi.queryByUserId(userId, UserHolder.getUserId());
        //构造访客数据，调用API保存
        Visitors visitors = new Visitors();
        visitors.setUserId(userId);
        visitors.setVisitorUserId(UserHolder.getUserId());
        visitors.setFrom("首页");
        visitors.setDate(System.currentTimeMillis());
        visitors.setVisitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        visitors.setScore(user.getScore());
        Boolean flag = visitorsApi.save(visitors);
        if (flag) {
            //如果保存成功 将时间存进redis
            String key = Constants.VISITORS_USER;   //hash的大键
            String hashKey = String.valueOf(visitors.getVisitorUserId());  //hash的小键     用户的id      被看人的id
            String value = String.valueOf(System.currentTimeMillis());   //被看的时间
            HashOperations<String, String, String> hash = redisTemplate.opsForHash();
            hash.put(key, hashKey, value);
        }
        //构造返回值
        return TodayBest.init(userInfo, user);
    }

    /**
     * 查看陌生人问题
     *
     * @param userId
     * @return
     */
    public String strangerQuestions(Long userId) {
        //调用api查询出对象
        Question question = questionApi.findByUserId(userId);
        //构造返回值
        if (ObjectUtil.isEmpty(question)) {
            return "螃蟹在剥我的壳";
        }
        return question.getTxt();
    }

    /**
     * 回复陌生人问题
     *
     * @param userId 用户id
     * @param reply  回复内容
     */
    public void strangerQues(Long userId, String reply) {
        //调用api查询接受消息的id
        UserInfo userInfo = userInfoApi.findById(UserHolder.getUserId());
        //构建回复消息
        Map map = new HashMap<>();
        map.put("userId", UserHolder.getUserId());
        map.put("huanXinId", Constants.HX_USER_PREFIX + UserHolder.getUserId());
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", strangerQuestions(userId));
        map.put("reply", reply);
        String jsonString = JSON.toJSONString(map);
        //发送消息
        Boolean aBoolean = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + userId, jsonString);
        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 探花-左滑右滑
     * /tanhua/cards
     */
    public List<TodayBest> cards() {
        //当前操作用户id
        Long userId = UserHolder.getUserId();
        //查询出的条数
        Integer index = 10;
        //查询出用户喜欢和不喜欢的集合
        List<RecommendUser> recommendUsers = recommendUserApi.findByUserId(userId, index);
        // 判断查询出的用户是否存在
        if (CollUtil.isEmpty(recommendUsers)) {
            //如果不存在,调用自己设置的用户
            recommendUsers = new ArrayList<>();
            //字符串分割设置的用户id
            String[] userIdS = recommendUser.split(",");
            for (String myUserId : userIdS) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(myUserId));
                recommendUser.setToUserId(UserHolder.getUserId());
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));
                recommendUsers.add(recommendUser);
            }
        }
        //获得userId这个字段所有的值
        List<Long> ids = CollUtil.getFieldValues(recommendUsers, "userId", Long.class);
        UserInfo userInfo = new UserInfo();
        //调用api查询获取对象
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);
        List<TodayBest> vos = new ArrayList<>();
        for (RecommendUser list : recommendUsers) {
            UserInfo info = map.get(list.getUserId());
            if (!ObjectUtil.isEmpty(info)) {
                TodayBest todayBest = TodayBest.init(info, list);
                vos.add(todayBest);
            }
        }
        return vos;
    }

    /**
     * 探花喜欢
     *
     * @param likeUserId
     */
    public void love(Long likeUserId) {
        //将数据保存的mongodb
        Boolean save = userLikeApi.save(UserHolder.getUserId(), likeUserId, true);
        if (!save) {
            throw new BusinessException(ErrorResult.error());
        }
        //操作redis喜欢的数据,删除不喜欢的数据
        SetOperations<String, String> set = redisTemplate.opsForSet();
        set.remove(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        set.add(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        //判断是否双向喜欢
        if (isLike(likeUserId, UserHolder.getUserId())) {
            //如果双向喜欢添加好友
            messagesService.contacts(likeUserId);
        }
    }

    public Boolean isLike(Long userId, Long likeUserId) {
        String key = Constants.USER_LIKE_KEY + userId;
        return redisTemplate.opsForSet().isMember(key, likeUserId.toString());
    }

    /**
     * 探花不喜欢
     *
     * @param likeUserId
     */
    public void unlove(Long likeUserId) {
        //将数据保存的mongodb
        Boolean save = userLikeApi.save(UserHolder.getUserId(), likeUserId, false);
        if (!save) {
            throw new BusinessException(ErrorResult.error());
        }
        //操作redis喜欢的数据,删除不喜欢的数据
        SetOperations<String, String> set = redisTemplate.opsForSet();
        set.add(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        set.remove(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());

        //判断是否双向喜欢
        if (isLike(likeUserId, UserHolder.getUserId())) {
            //如果双向喜欢添加好友
            messagesService.contacts(likeUserId);
        }
    }

    /**
     * 搜附近
     *
     * @param gender
     * @param distance
     * @return
     */
    public List<NearUserVo> search(String gender, String distance) {
        Long userId = UserHolder.getUserId();
        //通过当前操作用户的id查询出附近所有的人
        List<Long> userIds = userLocationApi.findByUser(userId, Double.valueOf(distance));
        //判断是否为空
        if (ObjectUtil.isEmpty(userIds)) {
            return new ArrayList<NearUserVo>();
        }
        UserInfo userInfo = new UserInfo();
        //查询出所有用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
        //判断返回的集合是否为空
        if (ObjectUtil.isEmpty(map)) {
            return new ArrayList<NearUserVo>();
        }
        //创建集合接收满足条件的对象
        List<NearUserVo> vos = new ArrayList<>();
        for (Long id : userIds) {
            UserInfo info = map.get(id);
            if (!ObjectUtil.isEmpty(info)) {
                NearUserVo nearUserVo = NearUserVo.init(info);
                vos.add(nearUserVo);
            }
        }
        return vos;
    }
}

