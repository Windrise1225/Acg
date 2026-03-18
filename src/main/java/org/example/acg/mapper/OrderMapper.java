package org.example.acg.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.acg.entity.Order;

import java.util.List;

@Mapper
public interface OrderMapper {

    List<Order> list();

    void insert(Order order);

    void update(Order order);

    List<Order> listByUserId(Integer userId);
}
