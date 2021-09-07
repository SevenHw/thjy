package com.tanhua.server.controller;

import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.service.HuanxinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-07 09:40
 **/
@RestController
@RequestMapping("/huanxin")
public class HuanxinController {
    @Autowired
    private HuanxinService huanxinService;

    /**
     * 环信登录
     * /huanxin/user
     */
    @GetMapping("/user")
    public ResponseEntity userLogin() {
        HuanXinUserVo vo = huanxinService.userLogin();
        return ResponseEntity.ok(vo);
    }
}
