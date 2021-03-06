package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domian.Announcement;
import com.tanhua.model.domian.User;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.*;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private FriendApi friendApi;

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private AnnouncementService announcementService;

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

    /**
     * 添加好友
     *
     * @param friendId 好友id
     */
    public void contacts(Long friendId) {
        //当前用户id
        Long userId = UserHolder.getUserId();
        //查看环信用户id
        User friendUser = userApi.findById(friendId);
        User user = userApi.findById(userId);
        //环信添加好友
        Boolean aBoolean = huanXinTemplate.addContact(user.getHxUser(), friendUser.getHxUser());
        //判断添加好友是否成功,如果失败则抛出异常
        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }
        //成功则给mongodb添加数据
        friendApi.save(friendId, userId);

    }

    /**
     * 分页查询好友列表
     *
     * @param page
     * @param pagesize
     * @param keyword
     * @return
     */
    public PageResult contactsGet(Integer page, Integer pagesize, String keyword) {
        //调用api查询好友数据卷
        List<Friend> list = friendApi.findByUserId(UserHolder.getUserId(), page, pagesize);
        //判断list 是否为空
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //获取到当中的friendId
        List<Long> friendIds = CollUtil.getFieldValues(list, "friendId", Long.class);
        //根据friendId查询用户数据
        UserInfo userInfo = new UserInfo();
        userInfo.setCity(keyword);
        Map<Long, UserInfo> map = userInfoApi.findByIds(friendIds, userInfo);
        //构建vo对象
        List<ContactVo> vos = new ArrayList<>();
        for (Friend friend : list) {
            UserInfo info = map.get(friend.getFriendId());
            if (!ObjectUtil.isEmpty(info)) {
                ContactVo contactVo = ContactVo.init(info);
                vos.add(contactVo);
            }
        }
        return new PageResult(page, pagesize, 0L, vos);
    }

    /**
     * 通用查询
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult inquire(CommentType like, Integer page, Integer pagesize) {
        if (CommentType.LIKE == like) {
            //调用api查询数据
            List<Comment> list = commentApi.findByUserId(like, UserHolder.getUserId(), page, pagesize);
            //判断是否为空
            if (CollUtil.isEmpty(list)) {
                return new PageResult();
            }
            //提出其中userId字段
            List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
            UserInfo userInfo = new UserInfo();
            //通过userId字段查询用户信息
            Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
            //船舰存放vo对象的集合
            List<CommentVo> vos = new ArrayList<>();
            //遍历查询出的Comment集合
            for (Comment comment : list) {
                UserInfo info = map.get(comment.getUserId());
                if (!ObjectUtil.isEmpty(info)) {
                    CommentVo vo = CommentVo.init(info, comment);
                    vos.add(vo);
                }
            }
            return new PageResult(page, pagesize, 0L, vos);
        } else if (CommentType.COMMENT == like) {
            //调用api查询数据
            List<Comment> list = commentApi.findByUserId(like, UserHolder.getUserId(), page, pagesize);
            //判断是否为空
            if (CollUtil.isEmpty(list)) {
                return new PageResult();
            }
            //提出其中userId字段
            List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
            UserInfo userInfo = new UserInfo();
            //通过userId字段查询用户信息
            Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
            //船舰存放vo对象的集合
            List<CommentVo> vos = new ArrayList<>();
            //遍历查询出的Comment集合
            for (Comment comment : list) {
                UserInfo info = map.get(comment.getUserId());
                if (!ObjectUtil.isEmpty(info)) {
                    CommentVo vo = CommentVo.init(info, comment);
                    vos.add(vo);
                }
            }
            return new PageResult(page, pagesize, 0L, vos);
        } else if (CommentType.LOVE == like) {
            //调用api查询数据
            List<Comment> list = commentApi.findByUserId(like, UserHolder.getUserId(), page, pagesize);
            //判断是否为空
            if (CollUtil.isEmpty(list)) {
                return new PageResult();
            }
            //提出其中userId字段
            List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
            UserInfo userInfo = new UserInfo();
            //通过userId字段查询用户信息
            Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
            //船舰存放vo对象的集合
            List<CommentVo> vos = new ArrayList<>();
            //遍历查询出的Comment集合
            for (Comment comment : list) {
                UserInfo info = map.get(comment.getUserId());
                if (!ObjectUtil.isEmpty(info)) {
                    CommentVo vo = CommentVo.init(info, comment);
                    vos.add(vo);
                }
            }
            return new PageResult(page, pagesize, 0L, vos);
        } else {
            //调用api查询信息
            List<Announcement> Announcementds = announcementService.list();
            List<AnnouncementVo> vos = new ArrayList<>();
            for (Announcement Announcementd : Announcementds) {
                AnnouncementVo init = AnnouncementVo.init(Announcementd);
                vos.add(init);
            }
            return new PageResult(page, pagesize, 0L, vos);
        }
    }


}
