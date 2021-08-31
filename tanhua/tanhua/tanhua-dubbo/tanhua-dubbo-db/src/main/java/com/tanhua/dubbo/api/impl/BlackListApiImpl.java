package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domian.BlackList;
import com.tanhua.model.domian.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi {


    @Autowired
    private BlackListMapper blackListMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 分页查询黑明单
     *
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<UserInfo> findByUserId(Long userId, int page, int size) {
        //1、构建分页参数对象Page
        Page pages = new Page(page, size);
        //2、调用方法分页（自定义编写 分页参数Page，sql条件参数）
        return userInfoMapper.findBlackList(pages, userId);
    }

    /**
     * 移除黑名单
     *
     * @param userId
     * @param blackUserId
     */
    @Override
    public void delete(Long userId, Long blackUserId) {
        QueryWrapper<BlackList> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        qw.eq("black_user_id", blackUserId);
        blackListMapper.delete(qw);

        /*String userid = userId.toString();
        String blackid = blackUserId.toString();
        blackListMapper.delete(userid,blackid);*/
    }
}
