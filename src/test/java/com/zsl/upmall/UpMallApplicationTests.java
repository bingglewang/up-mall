package com.zsl.upmall;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.entity.*;
import com.zsl.upmall.service.*;
import com.zsl.upmall.util.*;
import com.zsl.upmall.vo.SendMsgVo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.l;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpMallApplicationTests {

    @Autowired
    private OrderRefundService orderRefundService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GrouponOrderMasterService grouponOrderMasterService;

    @Autowired
    private GrouponActivitiesService grouponActivitiesService;

    @Test
    public void contextLoads() {
        //grouponOrderMasterService.doGrouponService(new Long(890),100);
        /*GrouponActivities grouponActivities = grouponActivitiesService.getById(9);
        grouponOrderMasterService.settlementGroup(103,grouponActivities);*/
       /* GrouponActivities grouponActivities = grouponActivitiesService.getById(9);
        grouponOrderMasterService.settlementGroup(150,grouponActivities);*/
       /* SendMsgVo sendMsgVo =  grouponOrderMasterService.sendMsg(new Long(781));
        System.out.println();*/
        /*doRebateBalance(new BigDecimal(5),3,163);*/
        refundToBalance();
    }

    public void refundToBalance(){
        LambdaQueryWrapper<OrderRefund> orderRefundQuery = new LambdaQueryWrapper<>();
        orderRefundQuery.isNull(OrderRefund::getRefundTime);
        List<OrderRefund> refundList = orderRefundService.list(orderRefundQuery);
        List<OrderRefund> updateBatch = new ArrayList<>();
        List<GrouponOrderMaster> grouponOrderMasterList = new ArrayList<>();
        for(OrderRefund item : refundList){
            OrderRefund updateRefund = new OrderRefund();
            GrouponOrderMaster grouponOrderMaster = new GrouponOrderMaster();
            updateRefund.setId(item.getId());
            OrderMaster orderMaster = orderMasterService.getById(item.getOrderId());

            if(orderMaster != null && orderMaster.getPayWay() - 3 == 0){
                //微信
                grouponOrderMaster.setMemberId(orderMaster.getMemberId());
                grouponOrderMaster.setOrderId(orderMaster.getId().intValue());

                updateRefund.setOutRefundNo(CharUtil.getCode(orderMaster.getMemberId(),3));
                updateRefund.setOutTradeNo(orderMaster.getSystemOrderNo());
                updateRefund.setTransactionId(orderMaster.getTransactionOrderNo());
                if(item.getTotalFee() == null || item.getTotalFee() - 0 == 0){
                    updateRefund.setTotalFee(Integer.valueOf(MoneyUtil.moneyYuan2FenStr(orderMaster.getPracticalPay())));
                    updateRefund.setRefundFee(Integer.valueOf(MoneyUtil.moneyYuan2FenStr(orderMaster.getPracticalPay())));
                }
                updateRefund.setRefundTime(new Date());
                grouponOrderMaster.setBackPrize(MoneyUtil.moneyFen2Yuan(updateRefund.getTotalFee().toString()));
                grouponOrderMasterList.add(grouponOrderMaster);
                updateBatch.add(updateRefund);
            }
        }
        boolean result = HttpClientUtil.deductUserBalanceBatch(false,grouponOrderMasterList);
        if(result){
            orderRefundService.updateBatchById(updateBatch);
        }
    }

}
