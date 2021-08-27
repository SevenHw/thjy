package com.tanhua.sso.service;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;

public class SendSms {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        // 访问的域名
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dysmsapi20170525.Client client = SendSms
                .createClient("**********", "**********");

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers( "17775627403") //目标手机号
                .setSignName("ABC商城") //签名名称
                .setTemplateCode("SMS_204756062") //短信模板code
                .setTemplateParam("{\"code\":\"1111\"}"); //模板中变量替换
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);

        SendSmsResponseBody body = sendSmsResponse.getBody();

        // code = OK 代表成功
        System.out.println(body.getCode() + "  " + body.getMessage());

    }

}