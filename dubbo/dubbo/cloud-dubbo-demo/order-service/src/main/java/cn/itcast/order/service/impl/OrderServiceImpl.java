package cn.itcast.order.service.impl;

import cn.itcast.user.service.OrderService;
import cn.itcast.dubbo.domain.Order;
import cn.itcast.order.mapper.OrderMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        return orderMapper.findById(orderId);
    }
}
