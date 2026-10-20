package com.cmp.mall.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与校验。
 *
 * <p>原版的登录态靠 HttpSession 存一个 user 属性，服务端要多实例共享 session 就必须做
 * 粘滞会话或 session 复制。改成无状态 JWT 后，多台 Tomcat 只要共用同一个密钥即可，
 * 这是"前后端分离 + 水平扩容"的前提。
 */
@Slf4j
@Component
public class JwtUtil {

    private final String secret;
    private final long expireMinutes;

    private SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expire-minutes}") long expireMinutes) {
        this.secret = secret;
        this.expireMinutes = expireMinutes;
    }

    @PostConstruct
    void init() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            // HS256 要求密钥不少于 256 位，长度不够时必须显式失败而不是运行期才报错
            throw new IllegalStateException("jwt.secret 至少需要 32 字节，当前 " + bytes.length);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generate(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMinutes * 60_000L);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 解析并校验签名与有效期。
     *
     * @return 用户名；token 无效或过期返回 null
     */
    public String parseUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("token 解析失败: {}", e.getMessage());
            return null;
        }
    }

    public long getExpireSeconds() {
        return expireMinutes * 60L;
    }
}
