package org.example.acg.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer productQuantity;
    private BigDecimal productPrice;
    private ZonedDateTime createTime;
    private ZonedDateTime lastModifiedTime;
    private String lastModifiedUserName;
}
