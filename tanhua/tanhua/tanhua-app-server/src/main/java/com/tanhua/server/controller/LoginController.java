package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 获取登录验证码
     * 请求参数：phone （Map）
     * 响应：void
     * ResponseEntity    指定返回值内容
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        //从前端获取输入的验证码
        String phone = (String) map.get("phone");
        //将验证码传入service层    与redis存如的验证码做比较是否正确
        userService.sendMsg(phone);
        return ResponseEntity.ok(null); //正常返回状态码200
    }

    /**
     * 校验登录
     * <p>
     * /user/loginVerification
     * <p>
     * phone  verificationCode
     */
    @PostMapping("loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map) {
        //1.调用map集合获取参数
        String phone = (String) map.get("phone");
        String code = (String) map.get("verificationCode");
        //2.调用userService完成用户登录
        Map retMap = userService.loginVerification(phone, code);
        //3.构造返回
        return ResponseEntity.ok(retMap);
    }


}