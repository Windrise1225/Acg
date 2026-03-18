package org.example.acg.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class Order {

    // id
    private Integer id;
    // 订单编号
    private String orderNumber;
    // 用户id
    private Integer userId;
    // 总价
    private BigDecimal sumPrice;
    // 创建时间
    private ZonedDateTime createTime;
    // 创建人
    private String createUserName;
    // 修改时间
    private ZonedDateTime lastModifiedTime;
    // 修改人
    private String lastModifiedUserName;
    // 订单状态
    private String status;
    // 备注
    private String remark;
}
