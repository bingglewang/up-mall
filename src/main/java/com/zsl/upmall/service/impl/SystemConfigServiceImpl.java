/**
 * @filename:SystemConfigServiceImpl 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2018 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service.impl;

import com.zsl.upmall.entity.SystemConfig;
import com.zsl.upmall.mapper.SystemConfigDao;
import com.zsl.upmall.service.SystemConfigService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**   
 * @Description:TODO(系统配置表
服务实现)
 *
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Service
public class SystemConfigServiceImpl  extends ServiceImpl<SystemConfigDao, SystemConfig> implements SystemConfigService  {
	
}