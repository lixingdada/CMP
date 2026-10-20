package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/** 用户资料 */
@Data
public class Account implements Serializable {
    private String userId;
    private String virtualName;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String avatar;
}
