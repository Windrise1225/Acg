package org.example.acg.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.Cart;

import java.util.List;

@Mapper
public interface CartMapper {

    List<Cart> list();

    void insert(Cart cart);

    void update(Cart cart);

    List<Cart> listByUserId(Integer userId);

    Cart getById(Integer id);

    Cart getByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId")Integer productId);

    void deleteById(Integer id);

    void changeQuantity(@Param("id") Integer id, @Param("delta") Integer delta);

    void clearCart(@Param("userId") Integer userId);

}
