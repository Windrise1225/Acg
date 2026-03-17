package org.example.acg.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> list();

    Product getProductByName(@Param("name") String name);

    List<Product> listProductLikeByName(@Param("name") String name);

    void insertProduct(Product product);

    boolean deleteProduct(@Param("id") Integer id);

    void updateProduct(Product product);
}
