package com.cmp.mall.security;

import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录拦截器。
 *
 * <p>从 Authorization 头里取 Bearer token 校验，通过后把用户名写进 {@link UserContext}，
 * 请求结束时清掉。不通过直接返回 401 JSON，不进 Controller。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 预检请求直接放行，否则浏览器拿不到 CORS 响应头
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String raw = request.getHeader(header);
        if (raw == null || !raw.startsWith(prefix)) {
            writeUnauthorized(response, "缺少登录凭证");
            return false;
        }

        String username = jwtUtil.parseUsername(raw.substring(prefix.length()).trim());
        if (username == null) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }

        UserContext.set(username);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 必须清理：线程会被复用
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.fail(ErrorCode.UNAUTHORIZED, message)));
    }
}
