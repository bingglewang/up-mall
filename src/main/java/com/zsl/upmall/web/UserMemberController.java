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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.*;
import com.zsl.upmall.exception.BusinessException;
import com.zsl.upmall.service.*;
import com.zsl.upmall.util.CountUtil;
import com.zsl.upmall.vo.in.user.UpdateUser;
import com.zsl.upmall.vo.in.user.UserBalacneIntegral;
import com.zsl.upmall.vo.out.user.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private OrderMasterService orderMasterService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuCustomService skuCustomService;

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
            if(update.getBalance() - 0 < 0){
                update.setBalance(new Double(0));
            }
            userMemberService.updateById(update);
        }

        if(userBalacneIntegral.getIntegral() != null){
            update.setIntegral(userMember.getIntegral() - userBalacneIntegral.getIntegral());
            if(update.getIntegral() - 0 < 0){
                update.setIntegral(0L);
            }
            userMemberService.updateById(update);
        }
        return result.success("修改成功");
    }


    /**
     * 用户升级(memberId, 等级标识)
     * @param userId
     * @param level 0 新人，1 铜牌，2 银牌，3金牌，4，事业伙伴，5经销商，6官方代理
     * @return
     */
    @GetMapping("updateUserLevel")
    public JsonResult updateUserLevel(Integer userId,Integer level) {
        UserMember userMember = userMemberService.getById(userId);
        if(userMember == null){
            return result.error("用户不存在");
        }
        if(level == null || level < 0 || level > 6){
            return  result.error("参数错误");
        }
        UserMember update = new UserMember();
        update.setId(userMember.getId());
        update.setUpdateTime(new Date());
        update.setUserLevel(level);
        if(!userMemberService.updateById(update)){
            throw new RuntimeException("升级失败");
        }
        return result.success("升级成功");
    }


    /**
     * 用户是否购买指定套餐（memberId, 套餐价）
     * @param userId  用户id
     * @param packagePrice 套餐价格
     * @return
     */
    @GetMapping("isBuyPackage")
    public JsonResult isBuyPackage(Integer userId,BigDecimal packagePrice) {
        UserMember userMember = userMemberService.getById(userId);
        if(userMember == null){
            return result.error("用户不存在");
        }
        if(packagePrice == null){
            return  result.error("参数错误");
        }

        QueryWrapper<OrderMaster> orderQuery = new QueryWrapper<>();
        orderQuery.eq("member_id",userId).and(orItem -> orItem.eq("order_status",SystemConfig.ORDER_STATUS_RECIEVE).or().eq("order_status",SystemConfig.ORDER_STATUS_FINISH));
        List<OrderMaster> orderMasters = orderMasterService.list(orderQuery);
        if(orderMasters.isEmpty()){
            // 没有购买
            return result.success(false);
        }

        //根据价格获取套餐sku_id
        List<SkuCustom> customList = skuCustomService.list();
        customList.stream().filter(item -> {
            Sku skuDetail = skuService.getById(item.getSkuId());
            if(skuDetail.getRetailPrice().compareTo(packagePrice) == 0){
                return true;
            }else{
                return false;
            }
        }).collect(Collectors.toList());

        CountUtil countFlag = new CountUtil();
        countFlag.setNum(0);
        orderMasters.stream()
                .forEach(order -> {
                    //拿到订单详情 列表sku_id
                    QueryWrapper<OrderDetail> detailQuery = new QueryWrapper<>();
                    detailQuery.eq("order_id",order.getId());
                    List<OrderDetail> detailList = orderDetailService.list(detailQuery);
                    detailList.stream().forEach(orderdetailItem -> {
                        customList.stream()
                                .forEach(customItem -> {
                                    if( orderdetailItem.getSkuId() - customItem.getSkuId() == 0){
                                        countFlag.add();
                                    }
                                });
                    });
                });
        if(countFlag.getNum() - 0 > 0){
            //购买过
            return result.success(true);
        }else{
            // 没有购买
            return result.success(false);
        }
    }


    /**
     * 按用户id查询推荐人用户对象（memberId, 推荐人类型标识）返回当前用户和符合条件的推荐人用户
     * @param userId 用户id
     * @return
     */
    @GetMapping("getUserShareInfoById/{id}")
    public JsonResult getUserShareInfoById(@PathVariable("id") Integer userId){
        //当前用户信息
        UserMember currentUser = userMemberService.getById(userId);
        if(currentUser == null){
            return result.error("用户不存在");
        }
        //会员推荐人信息
        UserMember vipShareUser = userMemberService.getById(currentUser.getMemberRecommendedId());
        //代理上推荐人信息
        UserMember agentShareUser = userMemberService.getById(currentUser.getAgentRecommendedId());
        Map<String,Object> mapInfo = new HashMap<>();
        mapInfo.put("currentUser",currentUser);
        mapInfo.put("vipShareUser",vipShareUser);
        mapInfo.put("agentShareUser",agentShareUser);
        return result.success(mapInfo);
    }

}