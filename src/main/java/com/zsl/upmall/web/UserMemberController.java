/**
 * @filename:UserMemberController 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSONObject;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.UserMember;
import com.zsl.upmall.exception.BusinessException;
import com.zsl.upmall.service.UserMemberService;
import com.zsl.upmall.vo.in.user.UpdateUser;
import com.zsl.upmall.vo.in.user.UserBalacneIntegral;
import com.zsl.upmall.vo.out.user.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>自动生成工具：mybatis-dsc-generator</p>
 * 
 * <p>说明： 用户表API接口层</P>
 * @version: V1.0
 * @author: binggleWang
 * @time    2020年04月08日
 *
 */
@RestController
@RequestMapping("/userMember")
public class UserMemberController{

    @Autowired
    private WxMaService wxService;

    @Autowired
    protected UserMemberService userMemberService;

    protected JsonResult result = new JsonResult();

    /**
     * @explain 用户详情
     * @return  JsonResult
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/userInfo")
    public JsonResult getById(@RequestBody JSONObject rawUserInfo,HttpServletRequest reques) throws Exception{
        //获取用户id
        RequestContext requestContext = RequestContextMgr.getLocalContext();
        //用户信息校验
        if (!wxService.getUserService().checkUserInfo(requestContext.getSessionKey(),rawUserInfo.getString("rawData"),rawUserInfo.getString("signature"))) {
            throw new BusinessException(400,"小程序用户信息校验失败，可能遭到非法串改");
        }

        // 解密用户信息
        WxMaUserInfo userInfo1 = wxService.getUserService().getUserInfo(requestContext.getSessionKey(), rawUserInfo.getString("encryptedData"), rawUserInfo.getString("iv"));
        if(userInfo1 != null){
            UserMember userUpdate = new UserMember();
            userUpdate.setId(requestContext.getUserId());
            userUpdate.setAvatar( userInfo1.getAvatarUrl());
            userUpdate.setGender(Integer.parseInt(userInfo1.getGender()));
            userUpdate.setNickName(userInfo1.getNickName());
            userMemberService.updateById(userUpdate);
        }

        UserMember obj=userMemberService.getById(requestContext.getUserId());
        if (null!=obj ) {
            obj=userMemberService.getById(requestContext.getUserId());
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(obj,userInfo);
            result.success(userInfo);
        }else {
            result.error("用户不存在！");
        }
        return result;
    }

    /**
     * @explain 用户修改
     * @param   updateUser 修改字段
     * @return  Boolean
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/update")
    public JsonResult update(@RequestBody UpdateUser updateUser){
        JsonResult result=new JsonResult();
        if (null!=updateUser) {
            //获取用户id
            RequestContext requestContext = RequestContextMgr.getLocalContext();
            UserMember update = new UserMember();
            update.setId(requestContext.getUserId());
            boolean rsg = false;
            if(StringUtils.isNotBlank(updateUser.getAvatar())){
                update.setAvatar(updateUser.getAvatar());
                rsg = userMemberService.updateById(update);
            }
            if(StringUtils.isNotBlank(updateUser.getRealName())){
                update.setRealName(updateUser.getRealName());
                rsg = userMemberService.updateById(update);
            }
            if(StringUtils.isNotBlank(updateUser.getIdentityCard())){
                update.setIdentityCard(updateUser.getIdentityCard());
                rsg = userMemberService.updateById(update);
            }
            if(StringUtils.isNotBlank(updateUser.getMobile())){
                update.setMobile(updateUser.getMobile());
                rsg =userMemberService.updateById(update);
            }
            if(StringUtils.isNotBlank(updateUser.getNickName())){
                update.setNickName(updateUser.getNickName());
                rsg =userMemberService.updateById(update);
            }
            if(updateUser.getGender() != null){
                update.setGender(updateUser.getGender());
                rsg = userMemberService.updateById(update);
            }
            if (rsg) {
                result.success("修改成功");
            }else {
                result.error("修改失败！");
            }
        }else {
            result.error("请传入正确参数！");
        }
        return result;
    }


    /**
     * 扣减用户积分，和余额
     * @param userBalacneIntegral
     * @return
     */
    @PostMapping("/updateUserBalanceOrIntegral")
    public JsonResult updateUserBalanceOrIntegral(@RequestBody UserBalacneIntegral userBalacneIntegral) {
        if(userBalacneIntegral.getUserId() == null){
            return result.error("参数错误");
        }

        UserMember userMember = userMemberService.getById(userBalacneIntegral.getUserId());
        if(userMember == null){
            return result.error("用户不存在");
        }
        UserMember update = new UserMember();
        update.setId(userMember.getId());
        if(userBalacneIntegral.getBalance() != null){
            update.setBalance(userMember.getBalance() - userBalacneIntegral.getBalance());
            userMemberService.updateById(update);
        }

        if(userBalacneIntegral.getIntegral() != null){
            update.setIntegral(userMember.getIntegral() - userBalacneIntegral.getIntegral());
            userMemberService.updateById(update);
        }
        return result.success("修改成功");
    }

}