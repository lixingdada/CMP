package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.ChangePasswordRequest;
import com.cmp.mall.dto.LoginRequest;
import com.cmp.mall.dto.LoginResponse;
import com.cmp.mall.dto.RegisterRequest;
import com.cmp.mall.entity.Account;
import com.cmp.mall.entity.Signon;
import com.cmp.mall.mapper.CartMapper;
import com.cmp.mall.mapper.UserMapper;
import com.cmp.mall.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 认证与用户注册。
 *
 * <p>坑：@Transactional 加在同类内部自调用的方法上是不生效的，走不到代理对象。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final CartMapper cartMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 注册。signon / account / cart 三张表的写入必须在同一个事务里，
     * 否则中途失败会留下一个能登录但没有资料、也没有购物车的残缺用户。
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse register(RegisterRequest request) {
        if (userMapper.countByUsername(request.username()) > 0) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }

        userMapper.insertSignon(request.username(), passwordEncoder.encode(request.password()));

        Account account = new Account();
        account.setUserId(request.username());
        account.setVirtualName(StringUtils.hasText(request.virtualName())
                ? request.virtualName() : request.username());
        account.setEmail(request.email());
        account.setPhone(request.phone());
        userMapper.insertAccount(account);

        // 注册即建车，避免后续加购时再判断"车不存在"
        cartMapper.insertCart(request.username());

        log.info("新用户注册成功 username={}", request.username());
        return buildLoginResponse(request.username(), account.getVirtualName(), null);
    }

    public LoginResponse login(LoginRequest request) {
        Signon signon = userMapper.findSignonByUsername(request.username());
        // 用户不存在与密码错误返回同一个错误，避免被枚举出哪些用户名已注册
        if (signon == null || !verifyAndUpgrade(request.username(), request.password(), signon.getPassword())) {
            throw new BizException(ErrorCode.LOGIN_FAILED);
        }

        Account account = userMapper.findAccountById(request.username());
        String virtualName = account == null ? request.username() : account.getVirtualName();
        String avatar = account == null ? null : account.getAvatar();
        log.info("用户登录成功 username={}", request.username());
        return buildLoginResponse(request.username(), virtualName, avatar);
    }

    /**
     * 校验密码，并顺手把历史明文数据升级为 BCrypt。
     *
     * <p>老库里 {@code signon.password} 存的是明文，直接改代码会让所有存量用户登录失败。
     * 这里的做法是：先判断存储值是否是 BCrypt 格式（以 $2a$/$2b$/$2y$ 开头）。
     * 是 —— 正常走 matches；不是 —— 按明文比对，通过后立即用 BCrypt 覆盖。
     * 这样用户无感知，登录一次完成一次迁移。
     */
    private boolean verifyAndUpgrade(String username, String raw, String stored) {
        if (stored == null) {
            return false;
        }
        boolean isBcrypt = stored.startsWith("$2a$") || stored.startsWith("$2b$")
                || stored.startsWith("$2y$");
        if (isBcrypt) {
            return passwordEncoder.matches(raw, stored);
        }
        if (stored.equals(raw)) {
            userMapper.updatePassword(username, passwordEncoder.encode(raw));
            log.info("检测到历史明文密码，已自动升级为 BCrypt username={}", username);
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String username, ChangePasswordRequest request) {
        Signon signon = userMapper.findSignonByUsername(username);
        if (signon == null || !verifyAndUpgrade(username, request.oldPassword(), signon.getPassword())) {
            throw new BizException(ErrorCode.LOGIN_FAILED, "原密码不正确");
        }
        userMapper.updatePassword(username, passwordEncoder.encode(request.newPassword()));
        log.info("用户修改密码 username={}", username);
    }

    private LoginResponse buildLoginResponse(String username, String virtualName, String avatar) {
        String token = jwtUtil.generate(username);
        return new LoginResponse(token, jwtUtil.getExpireSeconds(), username, virtualName, avatar);
    }
}
