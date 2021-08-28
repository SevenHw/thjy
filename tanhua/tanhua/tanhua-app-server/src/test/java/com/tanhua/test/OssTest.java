package com.tanhua.test;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.AppServerApplication;
import com.tanhua.autoconfig.template.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OssTest {


    @Autowired
    private OssTemplate template;

    @Test
    public void testTemplateUpload() throws FileNotFoundException {
        //1.图片路径
        String path = "C:\\Users\\lihao\\Pictures\\Saved Pictures\\zzf.jpg";
        //2.构造一个
        FileInputStream inputStream = new FileInputStream(new File(path));
        String imageUrl = template.upload(path, inputStream);
        System.out.println(imageUrl);
    }

    @Test
    public void ossTest() throws FileNotFoundException {
        //1.图片路径
        String path = "C:\\Users\\lihao\\Pictures\\Saved Pictures\\gsx250rtext.jpg";
        //2.构造一个
        FileInputStream inputStream = new FileInputStream(new File(path));
        //3.拼写图片路劲
        String filename = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + "/" + UUID.randomUUID().toString() + path.substring(path.lastIndexOf("."));
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "LTAI5tBs3uPATwzESYYbLZGS";
        String accessKeySecret = "1hDSJlHKHnCvyYTt5ymDmvDuZaaHpv";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写Byte数组。
        //byte[] content = "Hello OSS".getBytes();
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("sevenlikey", filename, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();


        //文件访问域名
        String url = "https://sevenlikey.oss-cn-shenzhen.aliyuncs.com/" + filename;
        System.out.println(url);
    }
}
