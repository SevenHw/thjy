package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserinfoServive {
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private OssTemplate ossTemplate;
    @Autowired
    private AipFaceTemplate aipFaceTemplate;


    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    /**
     * 更新用户头像
     *
     * @param headPhoto
     * @param id
     */
    public void updateHead(MultipartFile headPhoto, Long id) throws IOException {
        //1.将图片上传到阿里云oss
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        //2.调用百度云判断是否包含人脸
        boolean detece = aipFaceTemplate.detece(imageUrl);
        //2.1如果不包含人脸,抛出异常
        if (!detece) {
            throw new BusinessException(ErrorResult.faceError());
        } else {
            //2.2包含人脸,调用API更新
            UserInfo userInfo = new UserInfo();
            userInfo.setId(Long.valueOf(id));
            userInfo.setAvatar(imageUrl);
            userInfoApi.update(userInfo);
        }
    }

    /**
     * 根据id来查询
     *
     * @param id
     * @return
     */
    public UserInfoVo findById(Long id) {
        UserInfo userInfo = userInfoApi.findById(id);
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo);//copy同名同类型的属性
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }

    /**
     * 更新
     *
     * @param userInfo
     */
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }
}
