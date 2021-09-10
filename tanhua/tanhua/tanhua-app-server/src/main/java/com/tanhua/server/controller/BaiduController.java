package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-10 11:03
 **/

@RestController
@RequestMapping("baidu")
public class BaiduController {
    @Autowired
    private BaiduService baiduService;

    /**
     * 上报地理位置
     * /baidu/location
     *
     * @return
     */
    @PostMapping("location")
    public ResponseEntity location(@RequestBody Map param) {
        Double longitude = Double.valueOf(param.get("longitude").toString());
        Double latitude = Double.valueOf(param.get("latitude").toString());
        String address = param.get("addrStr").toString();
        baiduService.location(longitude, latitude, address);
        return ResponseEntity.ok(null);
    }
}
