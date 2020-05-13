/**
 * @filename:GrouponOrderMasterService 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.entity.GrouponOrderMaster;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsl.upmall.vo.out.GrouponListVo;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:TODO(服务层)
 * @version: V1.0
 * @author: binggleWang
 * 
 */
public interface GrouponOrderMasterService extends IService<GrouponOrderMaster> {
    /**
     * 拼团列表
     * @param page
     * @param grouponOrderId
     * @return
     */
    IPage<GrouponListVo> getGrouponListByPage(IPage<GrouponListVo> page,Integer grouponOrderId);

    /**
     * 小程序订阅消息推送
     */
    void push();

    /**
     * 拼团业务处理
     */
    void doGrouponService(Long orderId);
}