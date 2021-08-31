package com.tanhua.test;

import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class SmsTemplateTest {
    //15120166172
    //注入
    @Autowired
    private SmsTemplate smsTemplate;

    //测试
    @Test
    public void testSendSms() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            smsTemplate.sendSms("18975973962", "666666");
            Thread.sleep(1000);
        }
    }
}