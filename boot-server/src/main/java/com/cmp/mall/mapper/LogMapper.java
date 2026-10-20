package com.cmp.mall.mapper;

import com.cmp.mall.entity.UserLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LogMapper {

    int insert(UserLog log);

    List<UserLog> findRecentByUser(@Param("userId") String userId, @Param("limit") int limit);
}
