package com.tanhua.server.service;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class MovementService {
    @Autowired
    private OssTemplate ossTemplate;
    @DubboReference
    private MovementApi movementApi;

    /**
     * 发布动态
     *
     * @param movement
     * @param imageContent
     */
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1.判断发布动态内容是否存在
        if (StringUtils.isEmpty(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }
        //2.获取当前登录的用户id
        Long userId = UserHolder.getUserId();
        //3.将文件内容上传到阿里云oss,获取请求地址
        ArrayList<String> list = new ArrayList<>();
        for (MultipartFile medias : imageContent) {
            String upload = ossTemplate.upload(medias.getOriginalFilename(), medias.getInputStream());
            list.add(upload);
        }
        //4.将数据封装到Movement对象
        movement.setUserId(userId);
        movement.setMedias(list);
        //5.调用Api完成发布动态
        movementApi.publish(movement);
    }
}
