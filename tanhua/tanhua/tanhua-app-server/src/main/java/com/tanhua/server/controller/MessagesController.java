package com.tanhua.server.controller;

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
     * /messages/contacts
     * 查看好友列表
     */
    @GetMapping("contacts")
    public ResponseEntity contactsGet(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pagesize,
                                      String keyword) {

        PageResult vo = messagesService.contactsGet(page,pagesize,keyword);
        return ResponseEntity.ok(vo);
    }

}
