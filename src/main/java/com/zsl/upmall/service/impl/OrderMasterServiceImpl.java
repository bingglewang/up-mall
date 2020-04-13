/**
 * @filename:OrderMasterServiceImpl 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2018 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.entity.OrderMaster;
import com.zsl.upmall.mapper.OrderMasterDao;
import com.zsl.upmall.service.OrderMasterService;
import com.zsl.upmall.vo.out.OrderListVo;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**   
 * @Description:TODO(服务实现)
 *
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Service
public class OrderMasterServiceImpl  extends ServiceImpl<OrderMasterDao, OrderMaster> implements OrderMasterService  {

    @Override
    public IPage<OrderListVo> getOrderListByStatus(IPage<OrderListVo> page, Integer orderStatus, Integer userId) {
        return this.baseMapper.getOrderListByStatus(page,orderStatus,userId);
    }
}