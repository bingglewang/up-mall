package com.zsl.upmall.vo.in.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderProductVo {
    private Integer skuId;
    private Integer productCount;
    private BigDecimal productPrice;
}
