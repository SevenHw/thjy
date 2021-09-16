package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManageService;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-14 13:06
 **/
@RestController
@RequestMapping("manage")
public class ManageController {
    @Autowired
    private ManageService manageService;

    /**
     * 用户数据翻页
     * /manage/users
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = manageService.findAllUsers(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 用户基本资料
     * /manage/users/:userID
     */
    @GetMapping("users/{userId}")
    public ResponseEntity userUserId(@PathVariable("userId") Long UserId) {
        UserInfo userInfo = manageService.findAllUsers(UserId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 视频记录翻页
     * /manage/videos
     *
     * @param page
     * @param pagesize
     * @param uid      用户id
     * @return
     */
    @GetMapping("videos")
    private ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pagesize,
                                  Long uid) {
        PageResult pr = manageService.videos(page, pagesize, uid);
        return ResponseEntity.ok(pr);
    }

    /**
     * 动态分页
     * /manage/messages
     *
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    @GetMapping("messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid, Integer state) {
        PageResult pr = manageService.messages(page, pagesize, uid, state);
        return ResponseEntity.ok(pr);
    }

    /**
     * 动态详情
     * /manage/messages/:id
     *
     * @param movementsId 动态id
     * @return
     */
    @GetMapping("messages/{id}")
    public ResponseEntity messages(@PathVariable("id") String movementsId) {
        MovementsVo pr = manageService.messagesUserId(movementsId);
        return ResponseEntity.ok(pr);
    }

    /**
     * 评论列表翻页
     * /manage/messages/comments
     *
     * @param page
     * @param pagesize
     * @param sortProp  排序字段
     * @param sortOrder 升序降序
     * @param messageID 动态id
     * @return
     */
    @GetMapping("messages/comments")
    public ResponseEntity comments(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   String sortProp,
                                   String sortOrder,
                                   String messageID) {
        PageResult pr = manageService.comments(page, pagesize, sortProp, sortOrder, messageID);
        return ResponseEntity.ok(pr);
    }

}
