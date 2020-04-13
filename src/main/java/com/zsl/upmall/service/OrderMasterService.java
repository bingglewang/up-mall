/**
 * @filename:OrderMasterService 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.entity.OrderMaster;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsl.upmall.vo.out.OrderListVo;

/**
 * @Description:订单详情（服务层)
 * @version: V1.0
 * @author: binggleWang
 * 
 */
public interface OrderMasterService extends IService<OrderMaster> {
    IPage<OrderListVo> getOrderListByStatus(IPage<OrderListVo> page,Integer orderStatus, Integer userId);
}