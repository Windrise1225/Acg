package org.example.acg.service;


import jakarta.annotation.Resource;
import org.example.acg.entity.Cart;
import org.example.acg.mapper.CartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Resource
    private CartMapper cartMapper;

    public List<Cart> listByUserId(Integer userId) {
        return cartMapper.listByUserId(userId);
    }
}
