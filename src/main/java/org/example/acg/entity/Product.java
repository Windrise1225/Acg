package org.example.acg.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class Product {
    // 商品id
    private Integer id;
    // 商品名称
    private String name;
    // 商品介绍
    private String introduction;
    // 商品价格
    private BigDecimal price;
    // 商品数量
    private Integer quantity;
    // 商品图片
    private String image;
    // 创建时间
    private ZonedDateTime createTime;
}
