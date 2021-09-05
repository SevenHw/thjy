package com.tanhua.model.domian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 陌生人问题
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BasePojo {

    private Long id;
    private Long userId;
    //问题内容
    private String txt;

}