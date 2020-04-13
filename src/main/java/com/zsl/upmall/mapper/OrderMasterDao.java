/**
 * @filename:OrderMasterDao 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.vo.out.OrderListVo;
import org.apache.ibatis.annotations.Mapper;
import com.zsl.upmall.entity.OrderMaster;
import org.apache.ibatis.annotations.Param;

/**   
 * @Description:订单(数据访问层)
 *
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Mapper
public interface OrderMasterDao extends BaseMapper<OrderMaster> {
    IPage<OrderListVo> getOrderListByStatus(IPage<OrderListVo> page,@Param("orderStatus") Integer orderStatus, @Param("userId") Integer userId);
}
