package com.zsl.upmall.quatz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsl.upmall.entity.OrderMaster;
import com.zsl.upmall.entity.OrderRefund;
import com.zsl.upmall.service.OrderMasterService;
import com.zsl.upmall.service.OrderRefundService;
import com.zsl.upmall.util.CharUtil;
import com.zsl.upmall.util.DateUtil;
import com.zsl.upmall.util.HttpClientUtil;
import com.zsl.upmall.util.MoneyUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Configuration
@EnableScheduling
@CommonsLog
public class FissionWantedTask {

    @Autowired
    private OrderRefundService orderRefundService;

    @Autowired
    private OrderMasterService orderMasterService;

    @Scheduled(cron="0 0 14 * * ?")
    public void taskEndFissionWanted(){
        //每天凌晨00:15触发
        LambdaQueryWrapper<OrderRefund> orderRefundQuery = new LambdaQueryWrapper<>();
        orderRefundQuery.isNull(OrderRefund::getRefundTime);
        List<OrderRefund> refundList = orderRefundService.list(orderRefundQuery);
        List<OrderRefund> updateBatch = new ArrayList<>();
        for(OrderRefund item : refundList){
            //判断时间
            LocalDateTime add = item.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime now = DateUtil.getCurrent14().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime expire =  now.plusDays(1);
            if(add.isBefore(expire)){
                continue;
            }

            OrderRefund updateRefund = new OrderRefund();
            updateRefund.setId(item.getId());
            updateRefund.setRefundDesc(item.getRefundDesc());
            OrderMaster orderMaster = orderMasterService.getById(item.getOrderId());

            if(orderMaster != null && orderMaster.getPayWay() - 2 == 0){
                //微信
                updateRefund.setOutRefundNo(CharUtil.getCode(orderMaster.getMemberId(),3));
                updateRefund.setOutTradeNo(orderMaster.getSystemOrderNo());
                updateRefund.setTransactionId(orderMaster.getTransactionOrderNo());
                if(updateRefund.getTotalFee() == null || updateRefund.getTotalFee() - 0 == 0){
                    updateRefund.setTotalFee(Integer.valueOf(MoneyUtil.moneyYuan2FenStr(orderMaster.getPracticalPay())));
                    updateRefund.setRefundFee(Integer.valueOf(MoneyUtil.moneyYuan2FenStr(orderMaster.getPracticalPay())));
                }else{
                    updateRefund.setTotalFee(item.getTotalFee());
                    updateRefund.setRefundFee(item.getRefundFee());
                }
                // 调用 微信退款
                boolean result =  HttpClientUtil.doRefund(updateRefund);
                if(result){
                    updateRefund.setRefundTime(new Date());
                }
                updateBatch.add(updateRefund);
            }
        }
        orderRefundService.updateBatchById(updateBatch);
    }
}