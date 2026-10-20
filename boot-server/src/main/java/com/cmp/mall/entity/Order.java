package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 订单主表 */
@Data
public class Order implements Serializable {
    private Integer orderId;
    private String userId;
    private String orderName;
    private LocalDateTime orderDate;
    private String orderAddress;
    private String orderTel;
}
