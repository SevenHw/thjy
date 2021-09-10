package com.tanhua.test;

import com.tanhua.AppServerApplication;
import com.tanhua.dubbo.api.UserLocationApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class TestUserLocationApi {

    @DubboReference
    private UserLocationApi userLocationApi;

    @Test
    public void testUpdateUserLocation() {
        this.userLocationApi.updateLocation(1L, 112.868386,28.221215, "育新地铁站");
        this.userLocationApi.updateLocation(2L, 112.865776,28.220097, "北京石油管理干部学院");
        this.userLocationApi.updateLocation(3L, 112.870672,28.220941, "回龙观医院");
        this.userLocationApi.updateLocation(4L, 112.869253,28.22266, "奥林匹克森林公园");
        this.userLocationApi.updateLocation(5L, 112.87131,28.221633, "小米科技园");
        this.userLocationApi.updateLocation(6L, 112.868453,28.223177, "天安门");
        this.userLocationApi.updateLocation(7L, 112.871786,28.220511, "北京西站");
        this.userLocationApi.updateLocation(8L, 112.865318,28.219906, "北京首都国际机场");
        this.userLocationApi.updateLocation(9L, 112.865165,28.22215, "德云社(三里屯店)");
        this.userLocationApi.updateLocation(10L, 112.866117,28.222469, "清华大学");
        this.userLocationApi.updateLocation(41L, 112.866603,28.223543, "北京大学");
        this.userLocationApi.updateLocation(42L, 112.872316,28.223185, "天津大学(卫津路校区)");
    }
}