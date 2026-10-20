package com.cmp.mall.mapper;

import com.cmp.mall.entity.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {

    List<Item> findByProductId(@Param("productId") String productId);

    Item findById(@Param("itemId") String itemId);

    /**
     * 扣减库存。带 {@code qty >= ?} 条件，靠数据库行锁保证并发下不会扣成负数：
     * 影响行数为 0 即代表库存不足，由上层抛业务异常。
     *
     * @return 受影响行数，0 表示库存不足或条目不存在
     */
    int deductStock(@Param("itemId") String itemId, @Param("quantity") int quantity);

    int restoreStock(@Param("itemId") String itemId, @Param("quantity") int quantity);
}
