package com.zsl.upmall.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.entity.UserMember;
import com.zsl.upmall.service.RedisService;
import com.zsl.upmall.service.UserMemberService;
import com.zsl.upmall.vo.in.auth.WxLoginInfo;
import com.zsl.upmall.vo.out.user.UserInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>说明： 微信登录</P>
 * @version: V1.0
 * @author: binggleWang
 * @time    2020年04月08日
 *
 */
@RestController
@RequestMapping("/wxAuth")
public class WxAuthController {

    @Autowired
    private WxMaService wxService;

    @Autowired
    private UserMemberService userMemberService;

    @Autowired
    private RedisService redisService;

    /**
     * 微信登录
     * @return 登录结果
     */
    @GetMapping("login/{code}")
    public Object loginByWeixin(@PathVariable(required = true) String code) throws Exception {
        JsonResult result = new JsonResult();
        if (code == null) {
            return result.error("参数错误");
        }
        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult wxResult = this.wxService.getUserService().getSessionInfo(code);
            openId = wxResult.getOpenid();
            sessionKey = wxResult.getSessionKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return result.error("登录失败");
        }

        QueryWrapper<UserMember> params = new QueryWrapper<>();
        params.lambda().eq(UserMember::getWeixinOpenid,openId);
        UserMember user = userMemberService.getOne(params);
        if (user == null) {
            user = new UserMember();
            user.setWeixinOpenid(openId);
            user.setUserLevel(0);
            user.setBalance(0.00);
            user.setIntegral(0L);
            //生成分享码
            String shareid = UUID.randomUUID() + "";
            shareid = shareid.replace("-","");
            user.setShareId(shareid);
            user.setLastLoginTime(new Date());
            user.setAddTime(new Date());
            user.setUpdateTime(new Date());

            userMemberService.save(user);

        } else {
            UserMember update = new UserMember();
            update.setId(user.getId());
            update.setLastLoginTime(new Date());
            update.setUpdateTime(new Date());
            if (!userMemberService.updateById(update)) {
                return result.error("用户更新失败");
            }
        }

        // 生成 token
        String token = UUID.randomUUID().toString();

        //放入redis  user
        Map<String ,Object> userRedis = new HashMap<>();
        userRedis.put("sessionKey",sessionKey);
        userRedis.put("userId",user.getId());
        redisService.set(token,JSONObject.toJSONString(userRedis));
        redisService.expire(token,3600 * 24 );

        Map<Object, Object> resultData = new HashMap<Object, Object>();
        UserInfo userInfoVo = new UserInfo();
        BeanUtils.copyProperties(user,userInfoVo);
        resultData.put("token", token);
        resultData.put("userInfo", userInfoVo);
        return result.success(resultData);
    }
}
