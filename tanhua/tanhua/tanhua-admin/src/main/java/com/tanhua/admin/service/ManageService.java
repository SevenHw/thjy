package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.CommentsVo;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-14 13:07
 **/
@Service
public class ManageService {
    @DubboReference
    private UserInfoApi userInfoApi;
    @DubboReference
    private VideoApi videoApi;
    @DubboReference
    private MovementApi movementApi;

    /**
     * 用户资料翻页
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findAllUsers(Integer page, Integer pagesize) {
        //获取用户信息
        IPage iPage = userInfoApi.findAllIpage(page, pagesize);
        return new PageResult(page, pagesize, iPage.getTotal(), iPage.getRecords());
    }

    /**
     * 用户基本资料
     * /manage/users/:userID
     */
    public UserInfo findAllUsers(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        return userInfo;
    }

    /**
     * 视频记录翻页
     *
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    public PageResult videos(Integer page, Integer pagesize, Long userId) {
        return videoApi.findByUserIds(page, pagesize, userId);
    }

    /**
     * 动态分页
     *
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    public PageResult messages(Integer page, Integer pagesize, Long uid, Integer state) {
        //调用API查询数据 ：movment对象
        PageResult result = movementApi.findByUserId(uid, state, page, pagesize);
        //解析PageResult，获取Movment对象列表
        List<Movement> items = (List<Movement>) result.getItems();
        //一个Movment对象转化为一个Vo
        if (CollUtil.isEmpty(items)) {
            return new PageResult();
        }
        List<Long> userIds = CollUtil.getFieldValues(items, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : items) {
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        //4、构造返回值
        result.setItems(vos);
        return result;
    }

    /**
     * 动态详情
     *
     * @param movementsId
     * @return
     */
    public MovementsVo messagesUserId(String movementsId) {
        //更具动态地查询出动态信息
        Movement movement = movementApi.findById(movementsId);
        //查询用户信息
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());
        //构造返回值
        return MovementsVo.init(userInfo, movement);
    }

    /**
     * 评论列表翻页
     * /manage/messages/comments
     *
     * @param page
     * @param pagesize
     * @param sortProp  排序字段
     * @param sortOrder 升序降序
     * @param publishId 动态id
     * @return
     */
    public PageResult comments(Integer page, Integer pagesize, String sortProp, String sortOrder, String publishId) {
        //更具动态地查询出动态信息
        Movement movement = movementApi.findById(publishId);
        //更具动态地查询出评论信息
        List<Comment> list = movementApi.findByPublidshId(page, pagesize, sortProp, sortOrder, publishId);
        //判断是否为空
        if (ObjectUtil.isEmpty(list)) {
            return new PageResult();
        }
        //获得返回对象的id
        List<Long> userIdS = CollUtil.getFieldValues(list, "userId", Long.class);
        //查询这些用户的个人信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIdS, null);
        List<CommentsVo> vos = new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (!ObjectUtil.isEmpty(userInfo)) {
                CommentsVo vo = CommentsVo.init(userInfo, comment);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, Long.valueOf(vos.size()), vos);
    }
}
