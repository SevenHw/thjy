package com.tanhua.dubbo;

import com.tanhua.dubbo.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDBApplication.class)
public class ldWorkerTest {
    @Autowired
    private IdWorker idWorker;

    @Test
    public void test() {
        Long test = idWorker.getNextId("test");
        System.out.println(test);
    }
}
