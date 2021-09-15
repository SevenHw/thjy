package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManageService;
import com.tanhua.model.domian.UserInfo;
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
}
