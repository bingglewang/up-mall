/**
 * @filename:UserAddressService 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.entity.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zsl.upmall.vo.out.address.AddressInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:TODO(收货地址表服务层)
 * @version: V1.0
 * @author: binggleWang
 * 
 */
public interface UserAddressService extends IService<UserAddress> {
    AddressInfo addressInfo(Long addressId);
    IPage<AddressInfo> addressPage(IPage<AddressInfo> page, @Param("userId") Integer userId);
}