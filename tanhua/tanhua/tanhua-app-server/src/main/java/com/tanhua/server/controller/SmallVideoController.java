package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideoController {

    @Autowired
    private SmallVideosService videosService;

    /**
     * 发布视频
     * 接口路径：POST
     * 请求参数：
     * videoThumbnail：封面图
     * videoFile：视频文件
     * /smallVideos
     */
    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videosService.saveVideos(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频推荐列表
     *
     * @param page     当前页数
     * @param pagesize 页尺寸
     * @return
     */
    @GetMapping
    public ResponseEntity findVideos(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = videosService.findVideos(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 视频用户关注
     *
     * @param friendId
     * @return
     */
    @PostMapping("{uid}/userFocus")
    public ResponseEntity userFocus(@PathVariable("uid") Long friendId) {
        videosService.userFocus(friendId);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频用户取消关注
     * /smallVideos/:uid/userUnFocus
     *
     * @param friendId
     * @return
     */
    @PostMapping("{uid}/userUnFocus")
    public ResponseEntity userUnFocus(@PathVariable("uid") Long friendId) {
        videosService.userUnFocus(friendId);
        return ResponseEntity.ok(null);
    }

    /**
     * /smallVideos/:id/comments
     * 评论发布
     *
     * @param videoId 视频id
     * @param comment 评论
     * @return
     */
    @PostMapping("{id}/comments")
    public ResponseEntity comments(@PathVariable("id") String videoId, @RequestBody String comment) {
        videosService.comments(videoId, comment);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频点赞
     * /smallVideos/:id/like
     *
     * @param videoId
     * @return
     */
    @PostMapping("{id}/like")
    public ResponseEntity like(@PathVariable("id") String videoId) {
        videosService.like(videoId);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论点赞
     * /smallVideos/comments/:id/like
     *
     * @param videoId
     * @return
     */
    @PostMapping("comments/{id}/like")
    public ResponseEntity commentsLike(@PathVariable("id") String videoId) {
        videosService.commentsLike(videoId);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论点赞 - 取消
     * /smallVideos/comments/:id/dislike
     *
     * @param videoId
     * @return
     */
    @PostMapping("comments/{id}/dislike")
    public ResponseEntity commentsDisLike(@PathVariable("id") String videoId) {
        videosService.commentsDisLike(videoId);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频点赞  - 取消
     * /smallVideos/:id/dislike
     *
     * @param videoId
     * @return
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity commentsDisLikeVideo(@PathVariable("id") String videoId) {
        videosService.commentsDisLikeVideo(videoId);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论列表
     * /smallVideos/:id/comments
     *
     * @param videoId
     * @return
     */
    @GetMapping("comments/{id}/comments")
    public ResponseEntity comments(
            @PathVariable("id") String videoId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = videosService.commentsPr(videoId, page, pagesize);
        return ResponseEntity.ok(pr);
    }


}