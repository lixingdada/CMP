package com.cmp.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/** 商品条目（SKU），库存记在这一层 */
@Data
public class Item implements Serializable {
    private String itemId;
    private String productId;
    private BigDecimal listPrice;
    private BigDecimal unitCost;
    private Integer supplier;
    private String status;

    /** 规格属性，原库用 attr1~attr5 承载不同品类的差异项 */
    private String attr1;
    private String attr2;
    private String attr3;
    private String attr4;
    private String attr5;

    /** 库存数量 */
    private Integer qty;
}
