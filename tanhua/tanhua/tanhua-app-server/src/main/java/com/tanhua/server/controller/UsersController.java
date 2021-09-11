package com.tanhua.server.controller;

import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.service.UserService;
import com.tanhua.server.service.UserinfoServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserinfoServive userinfoServive;
    @Autowired
    private UserService userService;

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

    /**
     * 修改手机号码
     * 发送验证码
     * /users/phone/sendVerificationCode
     */
    @PostMapping("phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode() {
        //1.获取手机号发送验证码
        String mobile = UserHolder.getMobile();
        userService.sendMsg(mobile);
        return ResponseEntity.ok(null);
    }

    /**
     * 修改手机号码
     * 校验验证码
     * /users/phone/checkVerificationCode
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody Map map) {
        //1.获取验证码
        String code = (String) map.get("verificationCode");
        String phone = UserHolder.getMobile();
        //2.校验验证码
        Map verification = userService.checkVerificationCode(phone, code);
        //返回结果
        return ResponseEntity.ok(verification);
    }

    /**
     * 修改手机号码
     * /users/phone
     */
    @PostMapping("/phone")
    public ResponseEntity phone(@RequestBody Map map) {
        //1.获取验证码
        String phone = (String) map.get("phone");
        //调用userService
        userService.updatePhone(phone);
        //返回结果
        return ResponseEntity.ok(null);
    }

    /**
     * 互相喜欢，喜欢，粉丝 - 统计
     * /users/counts
     */
    @GetMapping("/counts")
    public ResponseEntity counts() {
        Map map = userService.counts();
        return ResponseEntity.ok(map);
    }


}
