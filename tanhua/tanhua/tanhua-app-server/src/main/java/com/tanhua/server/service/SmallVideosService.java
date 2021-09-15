package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {
    @Autowired
    private OssTemplate ossTemplate;

    //从调度服务器获取，一个目标存储服务器，上传
    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer webServer;// 获取存储服务器的请求URL

    @DubboReference
    private VideoApi videoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private CommentApi commentApi;


    /**
     * 发布视频
     *
     * @param videoThumbnail 封面文件
     * @param videoFile      视频
     */
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        //1.将图片上传到阿里云oss
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        //上传视频文件
        String name = videoFile.getOriginalFilename();
        String sufix = name.substring(name.lastIndexOf(".") + 1);
        //上传文件获得路径
        StorePath uploadFile = storageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), sufix, null);//文件输入流，文件长度，文件后缀，元数据
        String url = webServer.getWebServerUrl() + uploadFile.getFullPath();
        //封装对象
        Video video = new Video();
        video.setPicUrl(imageUrl);
        video.setVideoUrl(url);
        video.setUserId(UserHolder.getUserId());
        video.setCreated(System.currentTimeMillis());
        //调用api保存信息
        videoApi.save(video);
    }

    /**
     * 视频推荐列表
     *
     * @param page     当前页数
     * @param pagesize 页尺寸
     * @return
     */
    /*@Cacheable(
            value = "videos",
            key = "T(com.tanhua.server.interceptor.UserHolder).getUserId()+'_'+#page+'_'+#pagesize")*/
    public PageResult findVideos(Integer page, Integer pagesize) {
        //查看redis中是否有推荐视频
        String value = redisTemplate.opsForValue().get(Constants.VIDEOS_RECOMMEND + UserHolder.getUserId());
        if (!ObjectUtil.isEmpty(value)) {
            String[] split = value.split(",");
            if (((page - 1) * pagesize) < split.length) {
                List<Long> list = Arrays.stream(split).map(e -> Long.valueOf(e)).collect(Collectors.toList());
                //调用api查询视频列表
                PageResult videos = videoApi.findVid(list, page, pagesize);
                //判断是否为空
                if (ObjectUtil.isEmpty(videos)) {
                    return new PageResult();
                }
                List<Video> videoList = (List<Video>) videos.getItems();
                //获得userId字段
                List<Long> userIds = CollUtil.getFieldValues(videoList, "userId", Long.class);
                //调用api查询个人信息
                Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
                List<VideoVo> vos = new ArrayList<>();
                for (Video video : videoList) {
                    UserInfo userInfo = map.get(video.getUserId());
                    if (!ObjectUtil.isEmpty(userInfo)) {
                        VideoVo vo = VideoVo.init(userInfo, video);
                        Object Vovalue = redisTemplate.opsForHash().get(Constants.FOCUS_USER_KEY + UserHolder.getUserId(), vo.getUserId());
                        if (ObjectUtil.isEmpty(vo)) {
                            vo.setHasFocus(0);
                        } else {
                            String s = Vovalue.toString();
                            Integer index = Integer.valueOf(s);
                            vo.setHasFocus(index);
                        }
                        vos.add(vo);
                    }
                }
                videos.setItems(vos);
                return videos;
            }
        }
        //调用api查询视频列表
        PageResult videos = videoApi.findAll(page, pagesize);
        //判断是否为空
        if (ObjectUtil.isEmpty(videos.getItems())) {
            return new PageResult();
        }
        List<Video> list = (List<Video>) videos.getItems();
        //获得userId字段
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //调用api查询个人信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = map.get(video.getUserId());
            if (!ObjectUtil.isEmpty(userInfo)) {
                VideoVo vo = VideoVo.init(userInfo, video);
                Object Vovalue = redisTemplate.opsForHash().get(Constants.FOCUS_USER_KEY + UserHolder.getUserId(), vo.getUserId().toString());
                if (ObjectUtil.isEmpty(Vovalue)) {
                    vo.setHasFocus(0);
                } else {
                    String s = Vovalue.toString();
                    Integer index = Integer.valueOf(s);
                    vo.setHasFocus(index);
                }
                vos.add(vo);
            }
        }
        videos.setItems(vos);
        return videos;
    }

    /**
     * 视频关注
     *
     * @param friendId
     */
    public void userFocus(Long friendId) {

        //1、创建FollowUser对象，并设置属性
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(friendId);
        focusUser.setCreated(System.currentTimeMillis());
        //2、调用API保存
        videoApi.saveFollowUser(focusUser);
        //3、将关注记录存入redis中
        String key = Constants.FOCUS_USER_KEY + UserHolder.getUserId();
        String hashKey = String.valueOf(friendId);
        redisTemplate.opsForHash().put(key, hashKey, "1");
    }

    /**
     * 视频取消关注
     *
     * @param friendId
     */
    public void userUnFocus(Long friendId) {
        //从redis中查询出关注状态
        String key = Constants.FOCUS_USER_KEY + UserHolder.getUserId();
        String hashKey = String.valueOf(friendId);
        String value = (String) redisTemplate.opsForHash().get(key, hashKey);
        //查询数据库中是否有这条信息
        Boolean flag = videoApi.findByUserIdBool(UserHolder.getUserId(), friendId);
        if (ObjectUtil.isEmpty(value)) {
            if (flag) {
                //如果redis和数据库中都没查到信息,就报出异常
                throw new BusinessException(ErrorResult.error());
            }
        }
        //调用api删除表中信息
        videoApi.delete(UserHolder.getUserId(), friendId);
        //3、将记录在redis中是数据删除
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * /smallVideos/:id/comments
     * 发布评论
     *
     * @param videoId 视频id
     * @param content 评论
     * @return
     */
    public void comments(String videoId, String content) {
        //1、获取操作用户id
        //获得发布人的id
        Video video = videoApi.findById(videoId);
        Long videoUserId = video.getUserId();
        //封装数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setContent(content);
        comment.setUserId(UserHolder.getUserId());
        comment.setPublishUserId(videoUserId);
        comment.setCreated(System.currentTimeMillis());
        //调用api 添加数据
        commentApi.saveVideo(comment);
    }

    /**
     * 评论列表
     * /smallVideos/:id/comments
     *
     * @param videoId
     * @return
     */
    public PageResult commentsPr(String videoId, Integer page, Integer pagesize) {
        //获得评论数据
        List<Comment> comments = commentApi.findComments(videoId, CommentType.COMMENT, page, pagesize);
        //判断评论是否存在
        if (ObjectUtil.isEmpty(comments)) {
            return new PageResult();
        }
        //获得发布评论的id
        List<Long> publishUserIds = CollUtil.getFieldValues(comments, "publishUserId", Long.class);
        //调用api获得发布评论人的信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(publishUserIds, null);
        List<CommentVo> vos = new ArrayList<>();
        for (Comment comment : comments) {
            UserInfo userInfo = map.get(comment.getPublishUserId());
            if (!ObjectUtil.isEmpty(userInfo)) {
                //获得vo对象
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, 0L, vos);
    }

    /**
     * 评论点赞
     *
     * @param videoId
     */
    public void commentsLike(String videoId) {
        //获得发布人的id
        Comment video = commentApi.find(videoId);
        Long videoUserId = video.getUserId();
        //封装数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setPublishUserId(videoUserId);
        comment.setCreated(System.currentTimeMillis());
        //调用api添加
        commentApi.saveVideoComments(comment);
    }

    /**
     * 评论点赞 - 取消
     *
     * @param videoId
     */
    public void commentsDisLike(String videoId) {
        //判断是否点赞
        Boolean flag = commentApi.hasComment(videoId, UserHolder.getUserId(), CommentType.LIKE);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
        //封装数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        //调用api删除
        commentApi.deleteComments(comment);
    }

    /**
     * 视频点赞
     *
     * @param videoId
     */
    public void like(String videoId) {
        //获得发布人的id
        Video video = videoApi.findById(videoId);
        Long videoUserId = video.getUserId();
        Comment comment = new Comment();
        //封装 数据
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setPublishUserId(videoUserId);
        comment.setCreated(System.currentTimeMillis());
        //调用api添加数据
        commentApi.saveVideo(comment);
        //将数据存进redis
        redisTemplate.opsForHash().put(Constants.VIDEO_LIKE_HASHKEY + UserHolder.getUserId(), video.getUserId().toString(), "1");

    }

    /**
     * 视频点赞  - 取消
     * /smallVideos/:id/dislike
     *
     * @param videoId
     * @return
     */
    public void commentsDisLikeVideo(String videoId) {
        //判断是否点赞
        Boolean flag = commentApi.hasComment(videoId, UserHolder.getUserId(), CommentType.LIKE);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
        //获得发布人的id
        Video video = videoApi.findById(videoId);
        Long userId = video.getUserId();
        //封装数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());
        comment.setPublishUserId(userId);
        //调用api删除
        commentApi.DisVideo(comment);
        //删除redis中的数据
        redisTemplate.opsForHash().delete(Constants.VIDEO_LIKE_HASHKEY + UserHolder.getUserId(), video.getUserId().toString());
    }

}