package com.tanhua.admin.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domian.Admin;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 用户登录
     *
     * @param map
     * @return
     */
    public Map login(Map map) {
        //获取请求参数
        String userName = (String) map.get("username");  //用户名
        String password = (String) map.get("password");  //用户名
        String verificationCode = (String) map.get("verificationCode");  //用户名
        String uuid = (String) map.get("uuid");  //用户名
        //检验验证码是否正确
        String value = redisTemplate.opsForValue().get(Constants.CAP_CODE + uuid);
        if (ObjectUtil.isEmpty(value) || !verificationCode.equals(value)) {
            throw new BusinessException("验证码失效");
        }
        redisTemplate.delete(Constants.CAP_CODE + uuid);
        //根据用户名查询管理员对象
        QueryWrapper<Admin> qw = new QueryWrapper<Admin>().eq("username", userName);
        Admin admin = adminMapper.selectOne(qw);
        //判断账户密码是否正确
        String code = SecureUtil.md5(password);
        if (ObjectUtil.isEmpty(admin) || !code.equals(admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        //判断admin对象是否存在,密码是否一致
        Map tokenMap = new HashMap<>();
        tokenMap.put("userName", admin.getUsername());
        tokenMap.put("id", admin.getId());
        String token = JwtUtils.getToken(tokenMap);
        //生成token
        Map rMap = new HashMap();
        rMap.put("token", token);
        //构造返回值
        return rMap;
    }

    /**
     * 获取当前用户的用户资料
     *
     * @return
     */
    public AdminVo profile() {
        Long id = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(id);
        return AdminVo.init(admin);
    }
}
