package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domian.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsApiImpl implements SettingsApi {

    @Autowired
    private SettingsMapper settingsMapper;

    /**
     * 根据用户id查询
     *
     * @param userId
     * @return
     */
    @Override
    public Settings findByUserId(Long userId) {
        QueryWrapper<Settings> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        return settingsMapper.selectOne(qw);
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }
}
