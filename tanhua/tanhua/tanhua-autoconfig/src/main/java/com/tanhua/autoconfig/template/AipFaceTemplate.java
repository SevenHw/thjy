package com.tanhua.autoconfig.template;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AipFaceTemplate {

    @Autowired
    private AipFace aipFace;

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
        JSONObject res = aipFace.detect(imageUrl, imageType, options);
        System.out.println(res.toString(2));
        Integer error_code = (Integer) res.get("error_code");
        return error_code == 0;
    }
}
