package com.tanhua.model.vo;

import com.tanhua.model.domian.Announcement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-09 15:09
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVo {
    private String id;
    private String title;
    private String description;
    private String createDate;
    private String updated;

    public static AnnouncementVo init(Announcement announcement) {
        AnnouncementVo vo = new AnnouncementVo();
        vo.setId(announcement.getId().toString());
        vo.setTitle(announcement.getTitle());
        vo.setDescription(announcement.getDescription());
        Date date1 = announcement.getCreated();
        Date updated = announcement.getUpdated();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String time = format.format(date1);
        String time1 = format.format(updated);
        vo.setCreateDate(time);
        vo.setUpdated(time1);
        return vo;
    }
}
