/**
 * @filename:UserFriendController 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.web;

import com.zsl.upmall.aid.AbstractController;
import com.zsl.upmall.entity.UserFriend;
import com.zsl.upmall.service.UserFriendService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**   
 * <p>自动生成工具：mybatis-dsc-generator</p>
 * 
 * <p>说明： 用户好友表API接口层</P>
 * @version: V1.0
 * @author: binggleWang
 * @time    2020年04月08日
 *
 */
@RestController
@RequestMapping("/userFriend")
public class UserFriendController extends AbstractController<UserFriendService,UserFriend>{
	
}