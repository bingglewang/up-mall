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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.config.WxProperties;
import com.zsl.upmall.entity.GrouponActivities;
import com.zsl.upmall.entity.GrouponOrderMaster;
import com.zsl.upmall.entity.OrderMaster;
import com.zsl.upmall.mapper.GrouponOrderMasterDao;
import com.zsl.upmall.service.GrouponActivitiesService;
import com.zsl.upmall.service.GrouponOrderMasterService;
import com.zsl.upmall.service.OrderMasterService;
import com.zsl.upmall.util.DateUtil;
import com.zsl.upmall.vo.out.GrouponListVo;
import com.zsl.upmall.vo.out.OrderListVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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
        //订单信息
        OrderMaster orderDetail = orderMasterService.getById(orderId);
        Integer grouponActivityId = orderDetail.getGrouponActivityId();
        Integer joinGroupId = orderDetail.getGrouponOrderId();

        //获取拼团活动信息
        GrouponActivities activitieDetail = activitiesService.getById(grouponActivityId);
        if(activitieDetail == null){
            logger.info("拼团活动【【【"+grouponActivityId+"】】】不存在");
            return ;
        }


        // 判断是否自己开团
        if(joinGroupId - 0 == 0){
            //自己
        }else if(joinGroupId > 0){
            //参与别人的团
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