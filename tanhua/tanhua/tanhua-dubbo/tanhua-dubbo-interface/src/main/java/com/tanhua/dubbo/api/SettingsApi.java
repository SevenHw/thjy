package com.tanhua.dubbo.api;


import com.tanhua.model.domian.Settings;

public interface SettingsApi {
    //根据用户id查询
    Settings findByUserId(Long userId);

    //添加
    void save(Settings settings);

    //更新
    void update(Settings settings);

}
