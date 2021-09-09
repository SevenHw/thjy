package com.tanhua.server.controller;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-07 16:01
 **/
@RestController
@RequestMapping("messages")
public class MessagesController {
    @Autowired
    private MessagesService messagesService;

    /**
     * 根据环信的用户id查询用户详情
     * messages/userinfo
     *
     * @return
     */
    @GetMapping("userinfo")
    public ResponseEntity userinfo(String huanxinId) {
        UserInfoVo vo = messagesService.findByHxId(huanxinId);
        return ResponseEntity.ok(vo);
    }

    /**
     * /messages/contacts
     * 添加好友
     */
    @PostMapping("contacts")
    public ResponseEntity contacts(@RequestBody Map map) {
        String ouserId = map.get("userId").toString();
        Long userId = Long.valueOf(ouserId);
        messagesService.contacts(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 查看好友列表
     * /messages/contacts
     */
    @GetMapping("contacts")
    public ResponseEntity contactsGet(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pagesize,
                                      String keyword) {
        PageResult vo = messagesService.contactsGet(page, pagesize, keyword);
        return ResponseEntity.ok(vo);
    }

    /**
     * 点赞列表
     * /messages/likes
     */
    @GetMapping("likes")
    public ResponseEntity likes(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult vo = messagesService.inquire(CommentType.LIKE, page, pagesize);
        return ResponseEntity.ok(vo);
    }

    /**
     * 评论列表
     * /messages/comments
     */
    @GetMapping("comments")
    public ResponseEntity comments(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult vo = messagesService.inquire(CommentType.COMMENT, page, pagesize);
        return ResponseEntity.ok(vo);
    }

    /**
     * 喜欢列表
     * /messages/loves
     */
    @GetMapping("loves")
    public ResponseEntity loves(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult vo = messagesService.inquire(CommentType.LOVE, page, pagesize);
        return ResponseEntity.ok(vo);
    }

    /**
     * 公告列表
     * /messages/announcements
     */
    @GetMapping("announcements")
    public ResponseEntity announcements(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult vo = messagesService.inquire(CommentType.ANNOUNCEMENT, page, pagesize);
        return ResponseEntity.ok(vo);
    }

}
