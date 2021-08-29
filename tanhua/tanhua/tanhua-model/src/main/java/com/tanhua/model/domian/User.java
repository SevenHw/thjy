package com.tanhua.model.domian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor  //满参构造
@NoArgsConstructor  //无参构造
public class User extends BasePojo {
    private Long id;
    private String mobile;
    private String password;
}
