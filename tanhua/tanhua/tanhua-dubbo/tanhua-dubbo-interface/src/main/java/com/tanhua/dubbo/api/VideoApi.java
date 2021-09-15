package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface VideoApi {

    /**
     * 上传视频
     *
     * @param video
     */
    void save(Video video);

    /**
     * 根据vid分页查询视频
     *
     * @param list     vid集合
     * @param page     当前页
     * @param pagesize 查询多少条
     * @return
     */
    PageResult findVid(List<Long> list, Integer page, Integer pagesize);

    /**
     * 跟也查询视频
     *
     * @param page     当前页
     * @param pagesize 查询多少条
     * @return
     */
    PageResult findAll(Integer page, Integer pagesize);

    /**
     * 添加关注
     *
     * @param focusUser
     */
    void saveFollowUser(FocusUser focusUser);

    /**
     * 取消关注
     *
     * @param userId
     * @param friendId
     * @return
     */
    Boolean findByUserIdBool(Long userId, Long friendId);

    /**
     * 根据id删除
     *
     * @param userId
     * @param friendId
     */
    void delete(Long userId, Long friendId);

    /**
     * 根据id查询
     *
     * @param videoId
     * @return
     */
    Video findById(String videoId);

    /**
     * 更具id查询个人所有视频
     *
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult findByUserIds(Integer page, Integer pagesize, Long userId);
}