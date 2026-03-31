package org.example.acg.config.enums;


import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING_PAYMENT("PENDING_PAYMENT", "待支付"),
    PENDING_DELIVERY("PENDING_DELIVERY", "待发货"),
    SHIPPED("SHIPPED", "已发货"),
    DELIVERING("DELIVERING", "配送中"),
    DELIVERED("DELIVERED", "已送达"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELED("CANCELED", "已取消");

    private final String code;
    private final String value;

    OrderStatusEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }
    public static OrderStatusEnum fromCode(String code) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static OrderStatusEnum fromValue(String value) {
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
