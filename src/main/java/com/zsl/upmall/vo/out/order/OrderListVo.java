package com.zsl.upmall.vo.out.order;

import com.zsl.upmall.vo.out.product.OrderListProductVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderListVo {
    /** 合计 */
    private BigDecimal totalAmount;
    /** 订单状态 */
    private Integer orderStatus;
    /** 订单号 */
    private String orderSn;
    /** 订单id */
    private Integer orderId;
    /** 订单商品列表 */
    private List<OrderListProductVo> orderListProductList;
}
