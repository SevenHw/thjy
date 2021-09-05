package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentsService;
import com.tanhua.server.service.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MovementController {
    @Autowired
    private MovementService movementService;

    @Autowired
    private CommentsService commentsService;

    /**
     * 发布动态
     */
    @PostMapping
    public ResponseEntity movements(Movement movement,
                                    MultipartFile[] imageContent) throws IOException {
        movementService.publishMovement(movement, imageContent);
        return ResponseEntity.ok(null);
    }

    /**
     * 查询我的动态
     * /movements/all
     */
    @GetMapping("all")
    public ResponseEntity findByUserId(Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementService.findBuUserId(userId, page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询好友动态
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity movements(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementService.findFriendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询推荐动态
     * /movements/recommend
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/recommend")
    public ResponseEntity recommend(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementService.findRecommendMovements(page, pagesize);
        return ResponseEntity.ok(pr);
    }

    /**
     * 查询单条动态
     * /movements/:id
     */
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") String movementId) {
        MovementsVo vo = movementService.findById(movementId);
        return ResponseEntity.ok(vo);
    }

    /**
     * 动态点赞
     * /movements/:id/like
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String movementId) {
        Integer pr = commentsService.like(movementId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 取消点赞
     * /movements/:id/dislike
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String movementId) {
        Integer pr = commentsService.dislike(movementId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 喜欢
     * /movements/:id/love
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable("id") String movementId) {
        Integer pr = commentsService.love(movementId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 取消喜欢
     * /movements/:id/unlove
     *
     * @param movementId
     * @return
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable("id") String movementId) {
        Integer pr = commentsService.unlove(movementId);
        return ResponseEntity.ok(pr);
    }
}
