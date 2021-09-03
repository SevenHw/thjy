package com.tanhua.server.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.spring.util.ObjectUtils;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.Interceptor.UserHolder;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovementService {
    @Autowired
    private OssTemplate ossTemplate;
    @DubboReference
    private MovementApi movementApi;
    @DubboReference
    private UserInfoApi userInfoApi;

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

    /**
     * 查询个人动态
     *
     * @param userId   用户id
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findBuUserId(Long userId, Integer page, Integer pagesize) {
        //1.更具用户id,调用API查询个人动态
        PageResult pr = movementApi.findByUserId(userId, page, pagesize);
        //2.获取PageResult中的itme列表对象
        List<Movement> items = (List<Movement>) pr.getItems();
        //3.非空判断
        if (ObjectUtil.isEmpty(items)) {
            return pr;
        }
        //4.循环数据列表
        UserInfo userInfo = userInfoApi.findById(userId);
        ArrayList<MovementsVo> list = new ArrayList<>();
        for (Movement item : items) {
            //5.一个Movement构造一个vo 对象
            MovementsVo vo = MovementsVo.init(userInfo, item);
            list.add(vo);
        }
        //6.构建返回值
        pr.setItems(list);
        return pr;
    }
}
