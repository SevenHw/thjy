package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domian.Question;
import com.tanhua.model.domian.Settings;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.Interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettingsService {

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private SettingsApi settingsApi;

    @DubboReference
    private BlackListApi blackListApi;

    /**
     * 查询通用设置
     *
     * @return
     */
    public SettingsVo settings() {
        SettingsVo vo = new SettingsVo();
        //1、获取用户id
        Long userId = UserHolder.getUserId();
        vo.setId(userId);
        //2、获取用户的手机号码
        vo.setPhone(UserHolder.getMobile());
        //3、获取用户的陌生人问题
        Question question = questionApi.findByUserId(userId);
        String txt = question == null ? "你喜欢java吗？" : question.getTxt();
        vo.setStrangerQuestion(txt);
        //4、获取用户的APP通知开关数据
        Settings settings = settingsApi.findByUserId(userId);
        if (settings != null) {
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
            vo.setLikeNotification(settings.getLikeNotification());
        }
        return vo;
    }


    /**
     * 设置陌生人问题
     */
    public void saveQuestion(String content) {
        //1.获取当前用户id
        Long userId = UserHolder.getUserId();
        //2.调用api查询当前用户的陌生人问题
        Question question = questionApi.findByUserId(userId);
        //3.判断问题是否存在
        if (question == null) {
            //3.1如果不存在.保存
            question = new Question();
            question.setId(userId);
            question.setTxt(content);
            questionApi.save(question);
        } else {
            //3.2如果存在,更新
            question.setTxt(content);
            questionApi.update(question);
        }
    }

    /**
     * 通知设置
     *
     * @param map
     */
    public void saveSettings(Map map) {
        Boolean likeNotification = (Boolean) map.get("likeNotification");
        Boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        Boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");
        //1.获取当前用户id
        Long userId = UserHolder.getUserId();
        //2.根据id查询童虎通知设置
        Settings settings = settingsApi.findByUserId(userId);
        //3.判断
        if (settings == null) {
            //保存
            settings = new Settings();
            settings.setId(userId);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.save(settings);
        } else {
            //更新
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.update(settings);
        }
    }

    /**
     * 分页查询黑名单
     *
     * @param page
     * @param size
     * @return
     */
    public PageResult blacklist(int page, int size) {
        //1.获取当前用户的id
        Long userId = UserHolder.getUserId();
        //2.调用api查询用户的黑名单分页列表 Ipage对象
        IPage<UserInfo> iPage = blackListApi.findByUserId(userId, page, size);
        //3.对象转化,将查询的Ipage对象的内容封装到PageResult中
        PageResult pr = new PageResult(page, size, iPage.getTotal(), iPage.getRecords());
        //4.返回
        return pr;
    }

    /**
     * 移除黑名单
     *
     * @param blackUserId
     */
    public void deleteBlackList(Long blackUserId) {
        //1.获取当前用户的id
        Long userId = UserHolder.getUserId();
        blackListApi.delete(userId, blackUserId);
    }
}