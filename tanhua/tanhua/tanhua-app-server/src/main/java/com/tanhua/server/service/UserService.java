package com.tanhua.server.service;

import com.aliyuncs.utils.StringUtils;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.model.domian.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private SmsTemplate template;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private UserApi userApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserLikeApi userLikeApi;

    private final String check = "CHECK_CODE_";

    /**
     * 发送短信验证码
     *
     * @param phone 手机号码
     */
    public void sendMsg(String phone) {
        //1、随机生成6位数字
        //String code = RandomStringUtils.randomNumeric(6);
        String code = "123456";
        //2、调用template对象，发送手机短信
        //template.sendSms(phone,code);
        //3、将验证码存入到redis
        redisTemplate.opsForValue().set(check + phone, code, Duration.ofMinutes(5));
    }

    /**
     * 验证登录
     *
     * @param phone 手机号码
     * @param code  验证码
     * @return
     */
    public Map loginVerification(String phone, String code) {
        //1.从redis中获取下发的验证码
        String redisCode = redisTemplate.opsForValue().get(check + phone);
        //2.对验证码进行校验(验证码是否存在,是否和输入的验证码一致)
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            throw new BusinessException(ErrorResult.loginError());
        }
        //3.删除redis中的验证码
        redisTemplate.delete(check);
        boolean isNew = false;
        //4.通过手机号码查询用户
        User user = userApi.findByMobile(phone);
        //5.如果用户不存在,创建用户保存到数据库中
        if (user == null) {
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;
            //环信庄户密码
            String hxUser = "hx" + user.getId();
            String hxpassword = Constants.INIT_PASSWORD;
            Boolean create = huanXinTemplate.createUser(hxUser, hxpassword);
            if (create) {
                user.setHxUser(hxUser);
                user.setHxPassword(hxpassword);
                userApi.hxUpdate(user);
            }
        }
        //6.通过jwt生成token(存入id和手机号)
        Map tokenMap = new HashMap<>();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", phone);
        String token = JwtUtils.getToken(tokenMap);
        //7.构造返回值
        Map retMap = new HashMap();
        retMap.put("token", token);
        retMap.put("isNew", isNew);
        return retMap;
    }

    /**
     * 修改手机号码验证码校验
     */
    public Map checkVerificationCode(String phone, String code) {
        //1.从redis中获取下发的验证码
        String redisCode = redisTemplate.opsForValue().get(check + phone);
        boolean isNew = !StringUtils.isEmpty(redisCode) && redisCode.equals(code);
        //2.对验证码进行校验(验证码是否存在,是否和输入的验证码一致)
        //3.删除redis中的验证码
        redisTemplate.delete(check);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("verification", isNew);
        return map;
    }

    /**
     * 修改手机号码
     *
     * @param phone
     */
    public void updatePhone(String phone) {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //调用userApi修改手机号码
        userApi.update(phone, userId);
    }

    /**
     * 互相喜欢，喜欢，粉丝 - 统计
     */
    public Map counts() {
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        Map map = userLikeApi.loveUniversal(userId);
        return map;
    }


}