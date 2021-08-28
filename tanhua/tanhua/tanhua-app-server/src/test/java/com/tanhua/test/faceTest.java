package com.tanhua.test;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

import java.util.HashMap;

public class faceTest {
    //设置APPID/AK/SK
    public static final String APP_ID = "24765404";
    public static final String API_KEY = "h5edOsG9ELa6GU2O0eYdHWaE";
    public static final String SECRET_KEY = "08627xp98520qfNsyUNyLBoh6Grer3uU";

    public static void main(String[] args) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 调用接口
        String image = "https://sevenlikey.oss-cn-shenzhen.aliyuncs.com/2021/08/28/69ac4065-87b6-4388-9833-9e20a1e5671d.jpg";
        String imageType = "URL";

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));

    }
}