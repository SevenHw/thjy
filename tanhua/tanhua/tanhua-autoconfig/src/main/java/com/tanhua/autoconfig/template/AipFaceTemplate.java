package com.tanhua.autoconfig.template;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AipFaceTemplate {

    @Autowired
    private AipFace client;

    /**
     * 检测图片中是否包含人脸
     * true:包含
     * false:不包含
     *
     * @return
     */
    public boolean detece(String imageUrl) {

        // 调用接口
        String imageType = "URL";

        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");


        // 人脸检测
        JSONObject res = client.detect(imageUrl, imageType, options);
        System.out.println(res.toString(2));
        Integer error_code = (Integer) res.get("error_code");
        /**
         * error_code如果为0则返回true   否则返回false
         *  true   代表识别到人脸
         *  false  代表没有识别到人脸
         */
        return error_code == 0 ? true : false;
    }
}
