/**
 * @filename:UserMemberServiceImpl 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2018 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.service.impl;

import com.zsl.upmall.entity.UserMember;
import com.zsl.upmall.mapper.UserMemberDao;
import com.zsl.upmall.service.UserMemberService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**   
 * @Description:TODO(用户表服务实现)
 *
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Service
public class UserMemberServiceImpl  extends ServiceImpl<UserMemberDao, UserMember> implements UserMemberService  {
	
}