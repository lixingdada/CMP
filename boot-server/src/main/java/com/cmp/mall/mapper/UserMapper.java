package com.cmp.mall.mapper;

import com.cmp.mall.entity.Account;
import com.cmp.mall.entity.Receiver;
import com.cmp.mall.entity.Signon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户聚合：signon（凭证）+ account（资料）+ receiver（地址）。
 *
 * <p>原版把这三张表拆给 SignonDao / AccountDao / UserDao 三个类，注册时要按顺序调三次，
 * 中间失败就留下半个用户。这里合成一个 Mapper，注册的整体性交给 Service 层的事务。
 */
@Mapper
public interface UserMapper {

    Signon findSignonByUsername(@Param("username") String username);

    int countByUsername(@Param("username") String username);

    int insertSignon(@Param("username") String username, @Param("password") String encodedPassword);

    int updatePassword(@Param("username") String username, @Param("password") String encodedPassword);

    Account findAccountById(@Param("userId") String userId);

    int insertAccount(Account account);

    int updateAccount(Account account);

    Receiver findReceiverById(@Param("userId") String userId);

    int insertReceiver(Receiver receiver);

    int updateReceiver(Receiver receiver);
}
