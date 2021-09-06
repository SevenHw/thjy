package com.tanhua.test;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.tanhua.AppServerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-06 19:46
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class HuanXinTest {

    private EMService service;

    @Before
    public void init() {
        EMProperties properties = EMProperties.builder()
                .setAppkey("1142210906047366#demo")
                .setClientId("YXA6_772-GR4T8yivbZRTnCJZg")
                .setClientSecret("YXA6Q6vXlBcsZ2BAgXb8aHf28A-NpGk")
                .build();
         service = new EMService(properties);
    }

    @Test
    public void test() {
        service.user().create("user02", "123456").block();
    }
}
