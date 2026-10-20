package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;

/** 登录凭证。密码以 BCrypt 哈希存储，不再明文 */
@Data
public class Signon implements Serializable {
    private String username;
    private String password;
}
