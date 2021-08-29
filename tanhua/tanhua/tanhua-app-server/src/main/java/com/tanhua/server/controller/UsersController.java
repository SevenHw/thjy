package com.tanhua.server.controller;

import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.service.UserinfoServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserinfoServive userinfoServive;

    /**
     * 查询用户资料
     * 1.请求头token
     * 2.请求参数userID
     */
    @GetMapping
    public ResponseEntity users(Long userID) {
        //2.获取用户ID
        //判断用户id
        if (userID == null) {
            userID = UserHolder.getUserId();
        }
        UserInfoVo vo = userinfoServive.findById(userID);
        return ResponseEntity.ok(vo);
    }

    /**
     * 跟新用户资料
     *
     * @param userInfo
     * @return
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo) {
        //2.获取用户ID
        //3.给获取的UserInfo添加id
        userInfo.setId(UserHolder.getUserId());
        userinfoServive.update(userInfo);
        return ResponseEntity.ok(null);
    }
    /**
     * 更新用户头像
     * header
     */
    @PostMapping("/header")
    public ResponseEntity updateHeader(MultipartFile headPhoto) throws IOException {
        userinfoServive.updateHead(headPhoto, UserHolder.getUserId());
        return ResponseEntity.ok(null);
    }
}
