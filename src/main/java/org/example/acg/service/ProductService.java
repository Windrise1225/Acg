package org.example.acg.service;


import jakarta.annotation.Resource;
import org.example.acg.entity.Product;
import org.example.acg.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Resource
    private ProductMapper productMapper;

    public List<Product> list(){
        return productMapper.list();
    }

    public Product getProductByName(String name){
        return productMapper.getProductByName(name);
    }

    public List<Product> listProductLikeByName(String name){
        return productMapper.listProductLikeByName(name);
    }

    public void insertProduct(Product product){
        productMapper.insertProduct(product);
    }

    public void updateProduct(Product product){
        productMapper.updateProduct(product);
    }

    public boolean deleteProduct(Integer id){
        productMapper.deleteProduct(id);
        return true;
    }
}
