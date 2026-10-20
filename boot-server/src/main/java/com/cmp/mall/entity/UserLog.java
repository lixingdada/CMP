package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 操作日志。item_id / cart_id / order_id 为可空外键，做多态关联 */
@Data
public class UserLog implements Serializable {
    private Integer logId;
    private String userId;
    private String action;
    private LocalDateTime actionTime;
    private String itemId;
    private Integer cartId;
    private Integer orderId;
    private String details;
}
