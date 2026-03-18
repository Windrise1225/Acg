package org.example.acg.service;


import jakarta.annotation.Resource;
import org.example.acg.entity.Order;
import org.example.acg.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    public List<Order> list(){
        return orderMapper.list();
    }

    public void insert(Order order){
        orderMapper.insert(order);
    }

    public void update(Order order){
        orderMapper.update(order);
    }

    public List<Order> listByUserId(Integer userId){
        return orderMapper.listByUserId(userId);
    }
}
