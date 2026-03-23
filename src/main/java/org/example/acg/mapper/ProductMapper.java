package org.example.acg.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.Product;

import java.util.List;

@Mapper
public interface ProductMapper {

    void save(Product product);

    List<Product> list();

    Product getProductById(@Param("id") Integer id);

    Product getProductByName(@Param("name") String name);

    List<Product> listProductLikeByName(@Param("name") String name);

    void insertProduct(Product product);

    boolean deleteProduct(@Param("id") Integer id);

    void updateProduct(Product product);

    void decreaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
}
