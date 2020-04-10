/**
 * @filename:UserAddressServiceImpl 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2018 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zsl.upmall.entity.UserAddress;
import com.zsl.upmall.mapper.UserAddressDao;
import com.zsl.upmall.service.UserAddressService;
import com.zsl.upmall.vo.out.address.AddressInfo;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**   
 * @Description:TODO(收货地址表服务实现)
 *
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Service
public class UserAddressServiceImpl  extends ServiceImpl<UserAddressDao, UserAddress> implements UserAddressService  {

    @Override
    public AddressInfo addressInfo(Long addressId){
        return this.baseMapper.addressInfo(addressId);
    }

    @Override
    public IPage<AddressInfo> addressPage(IPage<AddressInfo> page, Integer userId) {
        return this.baseMapper.addressPage(page,userId);
    }
}