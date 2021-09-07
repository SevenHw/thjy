package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: tanhua
 * @author: seven
 * @create: 2021-09-05 15:38
 **/
@RestController
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    /**
     * 评论-提交
     *
     * @param map
     * @return
     */
    @PostMapping
    private ResponseEntity comments(@RequestBody Map map) {
        String movementId = (String) map.get("movementId");
        String comment = (String) map.get("comment");
        commentsService.comments(movementId, comment);
        return ResponseEntity.ok(null);
    }

    /**
     * 分页查询评论列表
     *
     * @param page       当前页数
     * @param pagesize   页尺寸
     * @param movementId 动态编号
     * @return
     */
    @GetMapping
    public ResponseEntity findComments(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       String movementId) {
        PageResult pr = commentsService.findComments(movementId, page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 评论点赞
     * /comments/:id/like
     */
    @GetMapping("/{id}/like")
    public ResponseEntity commentLike(@PathVariable("id") String movementId) {
        Integer pr = commentsService.commentLike(movementId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 评论取消点赞
     * /comments/:id/dislike
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity commentDislike(@PathVariable("id") String movementId) {
        Integer pr = commentsService.commentDislike(movementId);
        return ResponseEntity.ok(pr);
    }

}
