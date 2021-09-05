package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovementService {
    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 发布动态
     *
     * @param movement
     * @param imageContent
     */
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1.判断发布动态内容是否存在
        if (StringUtils.isEmpty(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }
        //2.获取当前登录的用户id
        Long userId = UserHolder.getUserId();
        //3.将文件内容上传到阿里云oss,获取请求地址
        ArrayList<String> list = new ArrayList<>();
        for (MultipartFile medias : imageContent) {
            String upload = ossTemplate.upload(medias.getOriginalFilename(), medias.getInputStream());
            list.add(upload);
        }
        //4.将数据封装到Movement对象
        movement.setUserId(userId);
        movement.setMedias(list);
        //5.调用Api完成发布动态
        movementApi.publish(movement);
    }

    /**
     * 查询个人动态
     *
     * @param userId   用户id
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findBuUserId(Long userId, Integer page, Integer pagesize) {
        //1.更具用户id,调用API查询个人动态
        PageResult pr = movementApi.findByUserId(userId, page, pagesize);
        //2.获取PageResult中的itme列表对象
        List<Movement> items = (List<Movement>) pr.getItems();
        //3.非空判断
        if (ObjectUtil.isEmpty(items)) {
            return pr;
        }
        //4.循环数据列表
        UserInfo userInfo = userInfoApi.findById(userId);
        ArrayList<MovementsVo> list = new ArrayList<>();
        for (Movement item : items) {
            //5.一个Movement构造一个vo 对象
            MovementsVo vo = MovementsVo.init(userInfo, item);
            list.add(vo);
        }
        //6.构建返回值
        pr.setItems(list);
        return pr;
    }

    /**
     * 查询好友动态
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        //1.获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2.调用API查询当前用户好友发布的动态
        List<Movement> list = movementApi.findFriendMovement(page, pagesize, userId);
        return getPageResult(page, pagesize, list);
    }

    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        //3.判断列表是否为空
        if (ObjectUtil.isEmpty(list)) {
            return new PageResult();
        }
        //4.提取动态发布的人的id列表
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //5.更具用户的id列表获取用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //6.一个Movement构造一个vo对象
        ArrayList<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : list) {
            UserInfo userInfo = map.get(movement.getUserId());
            if (!(ObjectUtil.isEmpty(userInfo))) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        //7.构造PageResult并返回
        return new PageResult(page, pagesize, 0L, vos);
    }

    /**
     * 查询推荐动态
     *
     * @param page     当前页
     * @param pagesize 一页显示几条
     * @return
     */
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        //1.从redis中获取推荐数据
        String rediskey = Constants.MOVEMENTS_RECOMMEND + UserHolder.getUserId();
        String redisvalue = redisTemplate.opsForValue().get(rediskey);
        //2.判断推荐数据是否存在
        List<Movement> list = Collections.EMPTY_LIST;
        if (StringUtils.isEmpty(redisvalue)) {
            //3.如果不存在调用API随机构造10条动态数据
            list = movementApi.randomMovements(pagesize);
        } else {
            //4.如果存在处理pid数据
            String[] values = redisvalue.split(",");
            //判断起始条数是否大于数组长度
            if (((page - 1) * pagesize) < values.length) {
                List<Long> pids = Arrays.stream(values).skip((page - 1) * pagesize)  // 跳过多少条
                        .limit(pagesize)  //查询多少条
                        .map(e -> Long.valueOf(e))    //将数据转换为Long类型
                        .collect(Collectors.toList());    //将数据封装成一个集合
                //5.调用api更具pid数组查询动态数据
                list = movementApi.findMovementsByPids(pids);

            }
        }
        //6.调用公共方法构造返回值
        return getPageResult(page, pagesize, list);
    }

    /**
     * 查询单条动态
     *
     * @param movementId
     * @return
     */
    public MovementsVo findById(String movementId) {
        //根据id查询动态
        Movement movement = movementApi.findById(movementId);
        //将movement转换成vo对象
        //判断movement是否为null
        if (!ObjectUtil.isNull(movement)) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            //返回vo对象
            return vo;
        } else {
            return null;
        }

    }


}
