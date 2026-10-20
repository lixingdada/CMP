package com.cmp.mall.service;

import com.cmp.mall.common.BizException;
import com.cmp.mall.common.ErrorCode;
import com.cmp.mall.dto.ProfileView;
import com.cmp.mall.dto.UpdateProfileRequest;
import com.cmp.mall.entity.Account;
import com.cmp.mall.entity.Receiver;
import com.cmp.mall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 个人中心：account（资料）+ receiver（默认地址） */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public ProfileView getProfile(String username) {
        Account account = userMapper.findAccountById(username);
        if (account == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "用户资料不存在");
        }
        Receiver receiver = userMapper.findReceiverById(username);
        return new ProfileView(
                account.getUserId(),
                account.getVirtualName(),
                account.getBirthday(),
                account.getEmail(),
                account.getPhone(),
                account.getAvatar(),
                receiver == null ? null : receiver.getReceiverName(),
                receiver == null ? null : receiver.getReceiverPhone(),
                receiver == null ? null : receiver.getReceiverAddr()
        );
    }

    /**
     * 更新资料。只更新请求里非空的字段，空字段保持原值——
     * 对应 XML 里 {@code <set>} + {@code <if>} 动态拼接，避免把未传的字段写成 NULL。
     */
    @Transactional(rollbackFor = Exception.class)
    public ProfileView updateProfile(String username, UpdateProfileRequest request) {
        Account account = new Account();
        account.setUserId(username);
        account.setVirtualName(request.virtualName());
        account.setBirthday(request.birthday());
        account.setEmail(request.email());
        account.setPhone(request.phone());
        account.setAvatar(request.avatar());
        userMapper.updateAccount(account);

        boolean hasReceiver = request.receiverName() != null
                || request.receiverPhone() != null
                || request.receiverAddr() != null;
        if (hasReceiver) {
            Receiver receiver = new Receiver();
            receiver.setUserId(username);
            receiver.setReceiverName(request.receiverName());
            receiver.setReceiverPhone(request.receiverPhone());
            receiver.setReceiverAddr(request.receiverAddr());
            if (userMapper.findReceiverById(username) == null) {
                userMapper.insertReceiver(receiver);
            } else {
                userMapper.updateReceiver(receiver);
            }
        }

        return getProfile(username);
    }
}
