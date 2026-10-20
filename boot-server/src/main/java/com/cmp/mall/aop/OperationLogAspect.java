package com.cmp.mall.aop;

import com.cmp.mall.entity.UserLog;
import com.cmp.mall.mapper.LogMapper;
import com.cmp.mall.security.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面。
 *
 * <p>用 {@code @AfterReturning} 而不是 {@code @Around}：只记录成功的业务动作，
 * 抛异常的不记。且此时业务事务已提交，日志写入失败也不会污染主流程。
 *
 * <p>日志写入自身做了 try-catch 兜底——审计日志不该因为写失败而让用户的下单请求报错。
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private static final int MAX_DETAIL_LENGTH = 500;

    private final LogMapper logMapper;
    private final ObjectMapper objectMapper;

    @AfterReturning(pointcut = "@annotation(operationLog)")
    public void record(JoinPoint joinPoint, OperationLog operationLog) {
        String username = UserContext.get();
        if (username == null) {
            return;
        }
        try {
            UserLog entity = new UserLog();
            entity.setUserId(username);
            entity.setAction(operationLog.action());
            entity.setActionTime(LocalDateTime.now());
            entity.setDetails(buildDetails(joinPoint, operationLog));
            logMapper.insert(entity);
        } catch (Exception e) {
            // 审计失败不影响业务
            log.warn("写操作日志失败 action={} err={}", operationLog.action(), e.getMessage());
        }
    }

    private String buildDetails(JoinPoint joinPoint, OperationLog operationLog) {
        if (!operationLog.withArgs()) {
            return operationLog.action();
        }
        String method = joinPoint.getSignature().toShortString();
        String args;
        try {
            // 用 Jackson 序列化而不是 Arrays.toString：record/DTO 能输出结构化内容
            args = objectMapper.writeValueAsString(
                    Arrays.stream(joinPoint.getArgs())
                            .filter(a -> a == null || !(a instanceof jakarta.servlet.ServletRequest)
                                    && !(a instanceof jakarta.servlet.ServletResponse))
                            .toArray());
        } catch (Exception e) {
            args = "args-unserializable";
        }
        String details = method + " " + args;
        return details.length() > MAX_DETAIL_LENGTH
                ? details.substring(0, MAX_DETAIL_LENGTH) : details;
    }
}
