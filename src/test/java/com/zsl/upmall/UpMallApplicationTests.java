package com.zsl.upmall;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.entity.*;
import com.zsl.upmall.service.*;
import com.zsl.upmall.util.*;
import com.zsl.upmall.vo.SendMsgVo;
import org.apache.commons.lang3.StringUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;

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

//@RunWith(SpringRunner.class)
//@SpringBootTest
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

   //@Test
    public void contextLoads() {
        //grouponOrderMasterService.doGrouponService(new Long(890),100);
        /*GrouponActivities grouponActivities = grouponActivitiesService.getById(9);
        grouponOrderMasterService.settlementGroup(103,grouponActivities);*/
       /* GrouponActivities grouponActivities = grouponActivitiesService.getById(9);
        grouponOrderMasterService.settlementGroup(150,grouponActivities);*/
       /* SendMsgVo sendMsgVo =  grouponOrderMasterService.sendMsg(new Long(781));
        System.out.println();*/
        /*doRebateBalance(new BigDecimal(5),3,163);*/
        //回调 改变订单状态
    }



}
