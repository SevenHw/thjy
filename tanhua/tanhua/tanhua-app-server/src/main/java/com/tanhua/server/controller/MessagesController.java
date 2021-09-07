package com.tanhua.server.controller;

import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
