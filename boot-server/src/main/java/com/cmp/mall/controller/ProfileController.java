package com.cmp.mall.controller;

import com.cmp.mall.common.Result;
import com.cmp.mall.dto.ProfileView;
import com.cmp.mall.dto.UpdateProfileRequest;
import com.cmp.mall.entity.UserLog;
import com.cmp.mall.mapper.LogMapper;
import com.cmp.mall.security.UserContext;
import com.cmp.mall.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "个人中心")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final LogMapper logMapper;

    @Operation(summary = "获取个人资料")
    @GetMapping("/profile")
    public Result<ProfileView> profile() {
        return Result.ok(profileService.getProfile(UserContext.require()));
    }

    @Operation(summary = "更新个人资料")
    @PutMapping("/profile")
    public Result<ProfileView> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Result.ok(profileService.updateProfile(UserContext.require(), request));
    }

    @Operation(summary = "我的操作日志")
    @GetMapping("/logs")
    public Result<List<UserLog>> logs(@RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return Result.ok(logMapper.findRecentByUser(UserContext.require(), safeLimit));
    }

    @Operation(summary = "健康检查（不需要登录）")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.ok("UP");
    }
}
