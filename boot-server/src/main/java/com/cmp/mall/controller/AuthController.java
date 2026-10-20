package com.cmp.mall.controller;

import com.cmp.mall.common.Result;
import com.cmp.mall.dto.ChangePasswordRequest;
import com.cmp.mall.dto.LoginRequest;
import com.cmp.mall.dto.LoginResponse;
import com.cmp.mall.dto.ProfileView;
import com.cmp.mall.dto.RegisterRequest;
import com.cmp.mall.security.UserContext;
import com.cmp.mall.service.AuthService;
import com.cmp.mall.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证", description = "注册、登录、当前用户、修改密码")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ProfileService profileService;

    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @Operation(summary = "当前登录用户")
    @GetMapping("/me")
    public Result<ProfileView> me() {
        return Result.ok(profileService.getProfile(UserContext.require()));
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(UserContext.require(), request);
        return Result.ok();
    }
}
