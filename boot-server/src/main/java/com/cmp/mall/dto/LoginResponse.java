package com.cmp.mall.dto;

/** 登录成功返回。token 由前端存起来，之后每个请求放在 Authorization 头 */
public record LoginResponse(
        String token,
        long expiresIn,
        String username,
        String virtualName,
        String avatar
) {
}
