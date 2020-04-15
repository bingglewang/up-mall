package com.zsl.upmall.vo.in;

import com.zsl.upmall.validator.FlagValidator;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateOrderVo {
    /**
     * 地址id
     */
    @NotNull(message = "地址不能为空")
    private Integer addressId;

    /**
     * 订单号(用于区分下单，和去支付)
     */
    private String orderSn;

    /**
     * 购物车id(购物车结算传购物车1，不是购物车，传0)
     */
    @FlagValidator(value = {"0","1"},message = "只能为0或者1")
    private Integer cartId;
    /**
     * 支付方式（1：支付宝，2：微信）
     */
    @FlagValidator(value = {"1","2"},message = "只能为1或者2")
    private Integer payWay;
    /**
     * 商家id
     */
    private Integer shopId;

    /**
     * 订单总金额
     */
    @NotNull(message = "总金额不能为空")
    private BigDecimal totalAmount;

    /**
     * 运费
     */
    @NotNull(message = "运费不能为空")
    private BigDecimal freight;

    /**
     * 商品id
     */
    private Integer productId;

    /**
     * 商品数量
     */
    private Integer productCount;
}
