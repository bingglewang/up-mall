package com.zsl.upmall.vo.out.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderListProductVo {
    /** 商品图片 */
    private String productImg;
    /** 商品名称 */
    private String productName;
    /** 商品数量 */
    private Integer productCount;
    /** 商品价格 */
    private BigDecimal productPrice;
}
