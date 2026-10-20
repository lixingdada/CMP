package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;

/** 收货地址 */
@Data
public class Receiver implements Serializable {
    private String userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddr;
}
