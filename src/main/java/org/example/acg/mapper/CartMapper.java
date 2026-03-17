package org.example.acg.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.Cart;

import java.util.List;

@Mapper
public interface CartMapper {

    List<Cart> listByUserId(@Param("userId") Integer userId);
}
