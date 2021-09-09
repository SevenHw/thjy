package com.tanhua.model.domian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @program: tanhua
 * @author: Seven
 * @create: 2021-09-09 11:53
 * <p>
 * 通知
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BasePojo {
    private Long id;  //公告id
    private String title;  //标题
    private String description;//描述
}
