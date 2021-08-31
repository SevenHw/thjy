package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domian.BlackList;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface BlackListMapper extends BaseMapper<BlackList> {
   /* @Delete("DELETE FROM tb_black_list WHERE user_id=#{userid} AND black_user_id =#{blackid}")
    void delete(@Param("userid") String userid, @Param("blackid") String blackid);*/
}