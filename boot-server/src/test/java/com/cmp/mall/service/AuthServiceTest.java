package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.LoginRequest;
import com.cmp.mall.dto.LoginResponse;
import com.cmp.mall.dto.RegisterRequest;
import com.cmp.mall.entity.Account;
import com.cmp.mall.entity.Signon;
import com.cmp.mall.mapper.CartMapper;
import com.cmp.mall.mapper.UserMapper;
import com.cmp.mall.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private CartMapper cartMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Signon signon(String password) {
        Signon s = new Signon();
        s.setUsername("test");
        s.setPassword(password);
        return s;
    }

    private Account account() {
        Account a = new Account();
        a.setUserId("test");
        a.setVirtualName("小李");
        return a;
    }

    @Test
    @DisplayName("老库里的明文密码，登录成功后会被改写成 BCrypt")
    void loginWithPlainTextUpgrades() {
        when(userMapper.findSignonByUsername("test")).thenReturn(signon("123456"));
        when(userMapper.findAccountById("test")).thenReturn(account());
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$fakeHash");
        when(jwtUtil.generate("test")).thenReturn("token-abc");

        LoginResponse resp = authService.login(new LoginRequest("test", "123456"));

        assertThat(resp.token()).isEqualTo("token-abc");
        assertThat(resp.virtualName()).isEqualTo("小李");
        verify(userMapper).updatePassword("test", "$2a$10$fakeHash");
        // 明文直接存进去就不该走 matches
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("已经是 BCrypt 的密码走正常比对，不会再改库")
    void loginWithBcrypt() {
        String stored = "$2a$10$abcdefghijklmnopqrstuv";
        when(userMapper.findSignonByUsername("test")).thenReturn(signon(stored));
        when(userMapper.findAccountById("test")).thenReturn(account());
        when(passwordEncoder.matches("123456", stored)).thenReturn(true);
        when(jwtUtil.generate("test")).thenReturn("token-abc");

        authService.login(new LoginRequest("test", "123456"));

        verify(userMapper, never()).updatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("密码错和用户不存在报同一个错，不给撞库留线索")
    void loginFailedIsUniform() {
        when(userMapper.findSignonByUsername("test")).thenReturn(signon("$2a$10$abcdefghijklmnopqrstuv"));
        when(passwordEncoder.matches("wrong", "$2a$10$abcdefghijklmnopqrstuv")).thenReturn(false);
        when(userMapper.findSignonByUsername("ghost")).thenReturn(null);

        assertThatThrownBy(() -> authService.login(new LoginRequest("test", "wrong")))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.LOGIN_FAILED);

        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost", "whatever")))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("注册时会顺手建购物车")
    void registerCreatesCart() {
        when(userMapper.countByUsername("newbie")).thenReturn(0);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$newHash");
        when(jwtUtil.generate("newbie")).thenReturn("token-new");

        RegisterRequest req = new RegisterRequest("newbie", "123456", null, null, null);
        authService.register(req);

        verify(userMapper).insertSignon("newbie", "$2a$10$newHash");
        verify(userMapper).insertAccount(any(Account.class));
        verify(cartMapper).insertCart("newbie");
    }

    @Test
    @DisplayName("注册时昵称留空就用用户名顶上")
    void registerUsesUsernameAsVirtualName() {
        when(userMapper.countByUsername("newbie")).thenReturn(0);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$newHash");
        when(jwtUtil.generate("newbie")).thenReturn("token-new");

        RegisterRequest req = new RegisterRequest("newbie", "123456", "", null, null);
        authService.register(req);

        verify(userMapper).insertAccount(any(Account.class));
    }

    @Test
    @DisplayName("用户名被占用时不再往下写")
    void registerDuplicateUsername() {
        when(userMapper.countByUsername("test")).thenReturn(1);

        RegisterRequest req = new RegisterRequest("test", "123456", null, null, null);
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BizException.class)
                .extracting(e -> ((BizException) e).getErrorCode())
                .isEqualTo(ErrorCode.USERNAME_EXISTS);

        verify(userMapper, never()).insertSignon(anyString(), anyString());
        verify(cartMapper, never()).insertCart(anyString());
    }

    @Test
    @DisplayName("改密码时原密码不对就拒绝")
    void changePasswordChecksOld() {
        when(userMapper.findSignonByUsername("test")).thenReturn(signon("$2a$10$abcdefghijklmnopqrstuv"));
        when(passwordEncoder.matches("bad", "$2a$10$abcdefghijklmnopqrstuv")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("test",
                new com.cmp.mall.dto.ChangePasswordRequest("bad", "newpass123")))
                .isInstanceOf(BizException.class);

        verify(userMapper, never()).updatePassword(anyString(), anyString());
    }
}
