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
        List<String> not_win_list = redisService.lgetall("CP_" + SystemConfig.GROUP_PREFIX + 167);
        System.out.println(not_win_list.size());
    }

    public void doRebateBalance(BigDecimal bounty, Integer notWinSize, Integer grouponOrderId) {
        if(notWinSize - 0 == 0){
            return ;
        }
        if(notWinSize - 1 == 0 ){
            List<String> redisNotWinList1 = new ArrayList<>();
            redisNotWinList1.add(bounty.toString());
            redisService.lpushList(SystemConfig.NOT_WIN_LIST_PREFIX + grouponOrderId, redisNotWinList1);
            return ;
        }
        //抽奖 (平分奖金)
        BigDecimal left = bounty.divide(new BigDecimal(2), 1);
        BigDecimal right = bounty.subtract(left);
        BigDecimal leftPointCount = new BigDecimal(notWinSize).divide(new BigDecimal(2), 1);
        BigDecimal rightPointCount = new BigDecimal(notWinSize).subtract(leftPointCount);
        BigDecimal leftAvg = left.divide(leftPointCount).setScale(2, BigDecimal.ROUND_DOWN);
        right = right.add(left.subtract(leftAvg.multiply(leftPointCount)));
        // 发送奖金到余额 (平均)
        List<BigDecimal> reusltLeft = Stream.iterate(1, k -> ++k)
                .limit(leftPointCount.intValue())
                .map(i -> leftAvg)
                .collect(Collectors.toList());
        // 随机发放
        List<Double> result = RedPacket.hb(right.doubleValue(), rightPointCount.intValue(), 0.01);
        List<BigDecimal> reusltRight = result.stream().map(item -> new BigDecimal(item).setScale(2, BigDecimal.ROUND_HALF_UP)).collect(Collectors.toList());
        List<BigDecimal> resultAll = new ArrayList<>();
        resultAll.addAll(reusltLeft);
        resultAll.addAll(reusltRight);
        List<String> redisNotWinList = resultAll.stream()
                .map(item -> item.toString()).collect(Collectors.toList());
        redisService.lpushList(SystemConfig.NOT_WIN_LIST_PREFIX + grouponOrderId, redisNotWinList);
    }

}
