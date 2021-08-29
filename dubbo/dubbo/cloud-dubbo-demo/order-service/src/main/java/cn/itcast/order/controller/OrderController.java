package cn.itcast.order.controller;

import cn.itcast.user.service.OrderService;
import cn.itcast.user.service.UserService;
import cn.itcast.dubbo.domain.Order;
import cn.itcast.dubbo.domain.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @DubboReference(check = false)
    private UserService userService;


    @GetMapping("{orderId}")
    public Order queryOrderByUserId(@PathVariable("orderId") Long orderId) {
        //根据id查询订单
        Order order = orderService.queryOrderById(orderId);
        //获取用户id
        Long userId = order.getUserId();
        //查询用户
        User user = userService.queryById(userId);
        //设置用户对象
        order.setUser(user);
        // 根据id查询订单并返回
        return order;
    }
}
