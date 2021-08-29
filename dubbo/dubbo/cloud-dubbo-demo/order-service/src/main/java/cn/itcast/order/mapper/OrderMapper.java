package cn.itcast.order.mapper;


import cn.itcast.dubbo.domain.Order;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper {

    @Select("select * from tb_order where id = #{id}")
    Order findById(Long id);
}
