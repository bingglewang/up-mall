/**
 * @filename:GrouponOrderMasterServiceImpl 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2018 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.config.WxProperties;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.*;
import com.zsl.upmall.mapper.GrouponOrderMasterDao;
import com.zsl.upmall.service.*;
import com.zsl.upmall.util.CharUtil;
import com.zsl.upmall.util.DateUtil;
import com.zsl.upmall.util.HttpClientUtil;
import com.zsl.upmall.util.RedPacket;
import com.zsl.upmall.vo.GroupOrderStatusEnum;
import com.zsl.upmall.vo.out.GrouponListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**   
 * @Description:TODO(服务实现)
 *
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Service
public class GrouponOrderMasterServiceImpl  extends ServiceImpl<GrouponOrderMasterDao, GrouponOrderMaster> implements GrouponOrderMasterService  {

    private final Logger logger = LoggerFactory.getLogger(GrouponOrderMasterServiceImpl.class);

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private GrouponActivitiesService activitiesService;

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private GrouponOrderMasterService grouponOrderMasterService;

    @Autowired
    private GrouponOrderService grouponOrderService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    public IPage<GrouponListVo> getGrouponListByPage(IPage<GrouponListVo> page, Integer grouponOrderId) {
        return this.baseMapper.getGrouponListByPage(page, grouponOrderId);
    }

    @Override
    public void push() {
        //1，配置
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(wxProperties.getAppId());
        config.setSecret(wxProperties.getAppSecret());
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);

        //2,推送消息
        WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                .toUser("oB2fF5N8dIYFq7UtfFfIVkt6jz-E")
                .templateId(getTemplateNameId("success"))
                .page("pages/index/index")
                .build();
        //创建一个参数集合
        ArrayList<WxMaSubscribeData> wxMaSubscribeData = new ArrayList<>();
        //第一个内容：
        WxMaSubscribeData wxMaSubscribeData1 = new WxMaSubscribeData();
        wxMaSubscribeData1.setName("thing1");
        wxMaSubscribeData1.setValue("你的车子需要挪动谢谢");
        wxMaSubscribeData.add(wxMaSubscribeData1);

        // 第二个内容：
        WxMaSubscribeData wxMaSubscribeData2 = new WxMaSubscribeData();
        wxMaSubscribeData2.setName("car_number2");
        wxMaSubscribeData2.setValue("粤A12345");
        wxMaSubscribeData.add(wxMaSubscribeData2);

        // 第二个内容：
        WxMaSubscribeData wxMaSubscribeData3 = new WxMaSubscribeData();
        wxMaSubscribeData3.setName("date3");
        wxMaSubscribeData3.setValue(DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        wxMaSubscribeData.add(wxMaSubscribeData3);

        //把集合给大的data
        subscribeMessage.setData(wxMaSubscribeData);

        try {
            wxMaService.getMsgService().sendSubscribeMsg(subscribeMessage);
        } catch (Exception e) {
            logger.info("推送失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void doGrouponService(Long orderId) {
        RequestContext requestContext = RequestContextMgr.getLocalContext();
        //订单信息
        OrderMaster orderDetail = orderMasterService.getById(orderId);
        Integer grouponActivityId = orderDetail.getGrouponActivityId();
        Integer joinGroupId = orderDetail.getGrouponOrderId();

        //获取拼团活动信息
        GrouponActivities activityDetail = activitiesService.getById(grouponActivityId);
        if(activityDetail == null){
            logger.info("拼团活动【【【"+grouponActivityId+"】】】不存在");
            return ;
        }

        //判断活动是否过期
        LocalDateTime end = activityDetail.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime nowDate = LocalDateTime.now();
        if(end.isBefore(nowDate)) {
            logger.info("活动过期：【【【"+grouponActivityId + "】】】");
            return ;
        }

        // 判断是否自己开团
        if(joinGroupId - 0 == 0){
            //自己
            GrouponOrder grouponOrder = new GrouponOrder();
            LocalDateTime endDate = nowDate.plusHours(activityDetail.getExpireHour());
            //开始时间
            Date createTime = Date.from( nowDate.atZone( ZoneId.systemDefault()).toInstant());
            //结束时间
            Date endTime = null;
            if(endDate.isBefore(end)){
                endTime = Date.from( endDate.atZone( ZoneId.systemDefault()).toInstant());
            }else{
                endTime = Date.from( end.atZone( ZoneId.systemDefault()).toInstant());
            }
            grouponOrder.setCreateTime(createTime)
                    .setEndTime(endTime)
                    .setGrouponActivitiesId(grouponActivityId)
                    .setGrouponCode(CharUtil.generateGrouponCode())
                    .setGrouponOrderNo(CharUtil.getCode(requestContext.getUserId(),activityDetail.getMode()))
                    .setGrouponOrderStatus(GroupOrderStatusEnum.HAVING.getCode())
                    .setUserMemberId(requestContext.getUserId());
            if(!grouponOrderService.save(grouponOrder)){
                logger.info("拼团主订单【【【"+grouponOrder.getGrouponOrderNo()+"】】】插入失败，用户id：【【【"+ requestContext.getUserId() +"】】】");
                return ;
            }
            joinGroupId = grouponOrder.getId();
            //生成凭证放redis
            List<String> vouchers = CharUtil.generateJoinGroupCode(activityDetail.getGroupCount());
            redisService.lpushList(SystemConfig.GROUP_PREFIX + joinGroupId,vouchers);
            redisService.expire(SystemConfig.GROUP_PREFIX + joinGroupId,activityDetail.getExpireHour() * 60 * 60);
            //如果抽奖团存放一个副本
            if(activityDetail.getMode() - 1 == 0){
                redisService.lpushList( "CP_" + SystemConfig.GROUP_PREFIX + joinGroupId,vouchers);
                redisService.expire("CP_" + SystemConfig.GROUP_PREFIX + joinGroupId,activityDetail.getExpireHour() * 60 * 60);
            }
        }
        GrouponOrderMaster grouponOrderMaster = new GrouponOrderMaster();
        grouponOrderMaster.setMemberId(requestContext.getUserId())
                .setOrderId(orderId.intValue())
                .setGrouponOrderId(joinGroupId);

        // 插入groupon-order-master
        insertGroupOrderMaster(grouponOrderMaster,joinGroupId,activityDetail.getMode(),requestContext.getUserId(),orderId);

        //如果符合条件则 结算
        if(redisService.lsize(SystemConfig.GROUP_PREFIX + joinGroupId) - 0 == 0){
            // 修改 g-o 的结算时间  (主订单拼团成功)
            GrouponOrder grouponOrder = new GrouponOrder();
            grouponOrder.setId(joinGroupId).setSettlementTime(new Date()).setGrouponOrderStatus(GroupOrderStatusEnum.SUCCESS.getCode());
            grouponOrderService.updateById(grouponOrder);

            //开始结算 修改子订单状态
            if(activityDetail.getMode() - 0 == 0){
                GrouponOrderMaster grouponOrderMasterUpdate = new GrouponOrderMaster();
                grouponOrderMasterUpdate.setGrouponStatus(GroupOrderStatusEnum.SUCCESS.getCode());
                LambdaQueryWrapper<GrouponOrderMaster> updateGroupOrderMasterQuery = new LambdaQueryWrapper<>();
                updateGroupOrderMasterQuery.eq(GrouponOrderMaster::getGrouponOrderId,joinGroupId);
                grouponOrderMasterService.update(grouponOrderMasterUpdate,updateGroupOrderMasterQuery);
            }else if(activityDetail.getMode() - 1 == 0){
                final int final_joinGroupId = joinGroupId;
                // 获得 抽奖凭证(列表)（副本弹出来
                List<String> win_list = new ArrayList<>();
                Stream.iterate(1,k -> ++k)
                        .limit(activityDetail.getPrizeCount())
                        .forEach(item -> {
                            String voucherItem = redisService.lpop("CP_" + SystemConfig.GROUP_PREFIX + final_joinGroupId);
                            win_list.add(voucherItem);
                        });
                // 剩下凭证
                List<String> not_win_list = redisService.lgetall("CP_" + SystemConfig.GROUP_PREFIX + joinGroupId);
                Map<String,String> win_memberIds = new HashMap<>();
                LambdaQueryWrapper<GrouponOrderMaster> updateGroupOrderMasterQuery = new LambdaQueryWrapper<>();
                updateGroupOrderMasterQuery.eq(GrouponOrderMaster::getGrouponOrderId,joinGroupId);
                List<GrouponOrderMaster> allOrderMaster = grouponOrderMasterService.list(updateGroupOrderMasterQuery);
                for(GrouponOrderMaster item :  allOrderMaster){
                    int not_win_count = 0;
                    List<String> voucherList = Arrays.asList(item.getVoucher().split(","));
                    for(String vou :  voucherList){
                        if(not_win_list.contains(vou)){
                            //没中奖的
                            not_win_count ++ ;
                            win_memberIds.put(vou,item.getMemberId()+","+item.getOrderId());
                        }
                    }
                    if(not_win_count > 0 && not_win_count < voucherList.size()){
                        splitOrder(item.getOrderId(),not_win_count);
                    }
                }

                //抽奖 (平分奖金)
                BigDecimal left = activityDetail.getBounty().divide(new BigDecimal(2),1);
                BigDecimal right = activityDetail.getBounty().subtract(left);
                BigDecimal leftPointCount =   new BigDecimal(not_win_list.size()).divide(new BigDecimal(2),1);
                BigDecimal rightPointCount = new BigDecimal(not_win_list.size()).subtract(leftPointCount);
                BigDecimal leftAvg = left.divide(leftPointCount).setScale(2,BigDecimal.ROUND_DOWN);
                right = right.add(left.subtract(leftAvg.multiply(leftPointCount)));
                // 发送奖金到余额 (平均)
                for(int i = 1; i <= leftPointCount.intValue();i++){
                    //HttpClientUtil.deductUserBalance(6,true,,requestContext.getToken(),leftAvg,orderDetail.getSystemOrderNo());
                }
                // 随机发放
                List<Double> result = RedPacket.hb(right.doubleValue(), rightPointCount.intValue(), 0.01);
                List<BigDecimal> reuslt1 = result.stream().map(item -> new BigDecimal(item).setScale(2,BigDecimal.ROUND_HALF_UP)).collect(Collectors.toList());
            }
        }
    }


    /**
     * 拆单 todo
     * @param orderId
     * @param notWinCount
     */
    public void splitOrder(Integer orderId,int notWinCount){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        OrderDetail orderDetail = orderDetailService.getOne(queryWrapper);
        if(queryWrapper == null){
            return ;
        }
        //失败
        OrderDetail notWinOrderDetail = new OrderDetail();
        BeanUtils.copyProperties(orderDetail,notWinOrderDetail);
        BigDecimal not_win_price = orderDetail.getGoodsPrice().multiply(new BigDecimal(notWinCount));
        OrderDetail winOrderDetail = new OrderDetail();
        winOrderDetail.setId(orderDetail.getId());
        winOrderDetail.setActualCount(orderDetail.getActualCount() - notWinCount);
        winOrderDetail.setGoodsCount(orderDetail.getGoodsCount() - notWinCount);
        winOrderDetail.setGoodsAmount(orderDetail.getGoodsAmount().subtract(not_win_price));
        winOrderDetail.setPracticalClearing(orderDetail.getPracticalClearing().subtract(not_win_price));
        orderDetailService.updateById(winOrderDetail);
        notWinOrderDetail.setPracticalClearing(not_win_price.add(notWinOrderDetail.getGoodsCarriage()));
        notWinOrderDetail.setGoodsAmount(not_win_price);
        notWinOrderDetail.setGoodsCount(notWinCount);
        notWinOrderDetail.setActualCount(notWinCount);
        orderDetailService.save(notWinOrderDetail);
    }

    /**
     * 插入grouponOrderMaster
     * @param grouponOrderMaster
     * @param groupOrderId
     * @param mode
     */
    public void insertGroupOrderMaster(GrouponOrderMaster grouponOrderMaster,Integer groupOrderId,Integer mode,Integer userId,Long orderId){
        if(mode - 0 == 0){
            // 判断用户是否参加当前团队，已参加则不再新增数据
            LambdaQueryWrapper<GrouponOrderMaster> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GrouponOrderMaster::getMemberId,userId);
            GrouponOrderMaster isExistOrderMaster = grouponOrderMasterService.getOne(queryWrapper);
            if(isExistOrderMaster == null){
                return ;
            }
            //普通
            String voucherItem = redisService.lpop(SystemConfig.GROUP_PREFIX + groupOrderId);
            grouponOrderMaster.setVoucher(voucherItem);
            grouponOrderMasterService.save(grouponOrderMaster);
        }else if(mode - 1 == 0){
            LambdaQueryWrapper<OrderDetail> orderDetailQuery = new LambdaQueryWrapper<>();
            orderDetailQuery.eq(OrderDetail::getOrderId,orderId).last("limit 1");
            OrderDetail detail = orderDetailService.getOne(orderDetailQuery);
            if(detail == null){
                return ;
            }
            StringBuilder c_vouchers = new StringBuilder();
            Stream.iterate(1,k -> ++k)
                    .limit(detail.getGoodsCount())
                    .forEach(item -> {
                        String voucherItem = redisService.lpop(SystemConfig.GROUP_PREFIX + groupOrderId);
                        c_vouchers.append(voucherItem + ",");
                    });
            grouponOrderMaster.setVoucher(c_vouchers.toString());
            grouponOrderMasterService.save(grouponOrderMaster);
        }
    }

    /**
     * 获取模板id
     * @param templateName
     * @return
     */
    public String getTemplateNameId(String templateName) {
        Map<String,String> template =  wxProperties.getTemplate().stream()
                .filter(item -> item.get("name").equals(templateName)).findAny().orElse(null);
        if(template != null){
            return template.get("templateId");
        }else{
            return "";
        }
    }

}