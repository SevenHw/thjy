package com.tanhua.dubbo.api.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tanhua.dubbo.api.AnnouncementService;
import com.tanhua.dubbo.mappers.AnnouncementMapper;
import com.tanhua.model.domian.Announcement;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-09 14:30
 **/
@DubboService
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {
}
