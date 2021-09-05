package com.tanhua.autoconfig.template;

import com.tanhua.autoconfig.properties.SmsProperties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SmsTemplate {

    private static final String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

    public SmsTemplate(SmsProperties properties) {
    }

    public void sendSms(String mobile, String code) {
/*
        try {
            //配置阿里云
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(properties.getAccessKey())
                    // 您的AccessKey Secret
                    .setAccessKeySecret(properties.getSecret());
            // 访问的域名
            config.endpoint = "dysmsapi.aliyuncs.com";

            com.aliyun.dysmsapi20170525.Client client =  new com.aliyun.dysmsapi20170525.Client(config);

            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(mobile)
                    .setSignName(properties.getSignName())
                    .setTemplateCode(properties.getTemplateCode())
                    .setTemplateParam("{\"code\":\""+code+"\"}");
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSms(sendSmsRequest);

            SendSmsResponseBody body = response.getBody();

            System.out.println(body.getMessage());

        }catch (Exception e) {
            e.printStackTrace();
        }*/


        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Url);

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");
        //生成验证码
        int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);

        String content = new String("您的验证码是：" + code + "。请不要把验证码泄露给其他人。");

        NameValuePair[] data = {//提交短信
                new NameValuePair("account", "C65629209"), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("password", "aac1a4a63bc9e94ecbd18a9655642cc0"), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
                new NameValuePair("mobile", mobile),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);

        try {
            client.executeMethod(method);

            String SubmitResult = method.getResponseBodyAsString();

            //System.out.println(SubmitResult);

            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String index = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");

            System.out.println(index);
            System.out.println(msg);
            System.out.println(smsid);

            if ("2".equals(index)) {
                System.out.println("短信提交成功");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}