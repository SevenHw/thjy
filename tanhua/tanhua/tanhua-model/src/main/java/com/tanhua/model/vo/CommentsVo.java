package com.tanhua.model.vo;

import com.tanhua.model.domian.UserInfo;
import com.tanhua.model.mongo.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-15 21:10
 * 动态详情下面的评论分页
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentsVo {
    private String id; //评论id
    private String nickname; //昵称
    private Integer userId; //评论人id
    private String content; //评论
    private String createDate; //评论时间

    public static CommentsVo init(UserInfo userInfo, Comment item) {
        CommentsVo vo = new CommentsVo();
        BeanUtils.copyProperties(userInfo, vo);
        BeanUtils.copyProperties(item, vo);
        Date date = new Date(item.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        vo.setId(item.getId().toHexString());
        return vo;
    }
}
