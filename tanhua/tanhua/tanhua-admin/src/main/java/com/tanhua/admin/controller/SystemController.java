package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成图片验证码
     */
    @GetMapping("/verification")
    public void verification(String uuid, HttpServletResponse response) throws IOException {
        //设置响应参数
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //通过工具类生成验证码对象（图片数据和验证码信息）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(299, 97);
        String code = captcha.getCode();  //1234
        //调用service，将验证码存入到redis
        // redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, code);
        //由于方便,验证码变成1234
        redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, "1234");
        //通过输出流输出验证码
        captcha.write(response.getOutputStream());
    }

    /**
     * 用户登录
     *
     * @param map
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 获取用户的信息
     */
    @PostMapping("/profile")
    public ResponseEntity profile() {
        AdminVo vo = adminService.profile();
        return ResponseEntity.ok(vo);
    }


}
