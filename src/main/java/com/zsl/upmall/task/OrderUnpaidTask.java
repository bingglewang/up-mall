package com.zsl.upmall.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.entity.OrderDetail;
import com.zsl.upmall.entity.OrderMaster;
import com.zsl.upmall.entity.Sku;
import com.zsl.upmall.service.OrderDetailService;
import com.zsl.upmall.service.OrderMasterService;
import com.zsl.upmall.service.SkuService;
import com.zsl.upmall.util.BeanUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

public class OrderUnpaidTask extends Task {
    private final Log logger = LogFactory.getLog(OrderUnpaidTask.class);
    private long orderId = -1;

    public OrderUnpaidTask(long orderId, long delayInMilliseconds){
        super("OrderUnpaidTask-" + orderId, delayInMilliseconds);
        this.orderId = orderId;
    }

    public OrderUnpaidTask(long orderId){
        super("OrderUnpaidTask-" + orderId, SystemConfig.ORDER_UNPAID);
        this.orderId = orderId;
    }

    @Override
    public void run() {
        logger.info("系统开始处理延时任务---订单超时未付款---" + this.orderId);

        OrderMasterService orderService = BeanUtil.getBean(OrderMasterService.class);
        OrderDetailService orderDetailService = BeanUtil.getBean(OrderDetailService.class);
        SkuService skuService = BeanUtil.getBean(SkuService.class);

        //判断订单是否存在
        OrderMaster order = orderService.getById(this.orderId);
        if(order == null){
            return;
        }

        //判断订单状态是否为待付款
        if(order.getOrderStatus() - SystemConfig.ORDER_STATUS_WAIT_PAY != 0){
            return ;
        }

        // 设置订单已取消状态
        OrderMaster upOrderCancel = new OrderMaster();
        upOrderCancel.setId(order.getId());
        upOrderCancel.setOrderStatus(SystemConfig.ORDER_STATUS_CANCLE );
        upOrderCancel.setCancelTime(new Date());
        if (!orderService.updateById(upOrderCancel)) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",this.orderId);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailWrapper);

        for (OrderDetail orderGoods : orderDetails) {
            Sku  skuDetail  = skuService.getById(orderGoods.getSkuId());
            Sku addStock = new Sku();
            addStock.setId(skuDetail.getId());
            addStock.setStock(skuDetail.getStock() + orderGoods.getGoodsCount());
            if ( !skuService.updateById(addStock)) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }
        logger.info("系统结束处理延时任务---订单超时未付款---" + this.orderId);
    }
}
