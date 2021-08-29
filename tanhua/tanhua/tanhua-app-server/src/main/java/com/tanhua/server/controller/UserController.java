package com.tanhua.server.controller;

import com.tanhua.model.domian.UserInfo;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.service.UserinfoServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserinfoServive userinfoServive;

    /**
     * 保存用户信息
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo) {

        //2.向userinfo中设置用户id
        userInfo.setId(UserHolder.getUserId());
        //3.调用service
        userinfoServive.save(userInfo);
        return ResponseEntity.ok(null);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto) throws IOException {
        //2.向userinfo中设置用户id
        //3.调用service
        userinfoServive.updateHead(headPhoto, UserHolder.getUserId());
        return ResponseEntity.ok(null);
    }
}
