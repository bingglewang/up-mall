package com.zsl.upmall.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.UserFriend;
import com.zsl.upmall.entity.UserMember;
import com.zsl.upmall.service.RedisService;
import com.zsl.upmall.service.UserFriendService;
import com.zsl.upmall.service.UserMemberService;
import com.zsl.upmall.task.OrderUnpaidTask;
import com.zsl.upmall.vo.out.user.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private final Log logger = LogFactory.getLog(WxAuthController.class);

    @Autowired
    private WxMaService wxService;

    @Autowired
    private UserMemberService userMemberService;

    @Autowired
    protected UserFriendService baseService;

    @Autowired
    private RedisService redisService;

    /**
     * 微信登录
     * @return 登录结果
     */
    @GetMapping("login/{code}")
    public Object loginByWeixin(@PathVariable(required = true) String code,HttpServletRequest request) throws Exception {
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
            //新用户，如果别人分享来的则绑定关系
            String shareId = request.getParameter("shareId");
            if(StringUtils.isNotBlank(shareId)){
                JsonResult bingResult = shareBind(shareId,user.getId());
                logger.info("分享人分享码{{"+shareId+"}}"+"::用户{{"+user.getId()+"}}--->绑定结果"+bingResult.toString());
            }

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

    /**
     * @explain 分享绑定用户关系
     * @param shareId 分享人的分享码
     * @return  Boolean
     * @author  binggleWang
     * @time    2020年04月11日
     */
    @PostMapping("/shareBind/{id}")
    public JsonResult shareBind(@PathVariable("id") String shareId,Integer currentUserId){
        JsonResult result = new JsonResult();
        //获取分享人
        QueryWrapper<UserMember> shareQuery = new QueryWrapper<>();
        shareQuery.eq("share_id",shareId);
        UserMember shareMember = userMemberService.getOne(shareQuery);
        if(shareMember == null){
            return result.error("分享人不存在");
        }
        //判断该用户有没有被绑定
        if(getByFriendId(currentUserId) != null){
            return result.error("该用户已被绑定");
        }
        //绑定关系
        UserFriend insert = new UserFriend();
        insert.setCreateTime(new Date());
        insert.setUserId(shareMember.getId());
        insert.setFriendId(currentUserId);
        //获得分享人上一级
        UserFriend shareUpUser = getByFriendId(shareMember.getId());
        if(shareUpUser == null){
            insert.setTree(shareMember.getId()+"/" + currentUserId);
        }else{
            insert.setTree(shareUpUser.getTree()+"/" + currentUserId);
        }
        if(!baseService.save(insert)){
            throw new RuntimeException("绑定失败");
        }
        return result.success("绑定成功");
    }


    public UserFriend getByFriendId(Integer friendId){
        QueryWrapper<UserFriend> friendQuery = new QueryWrapper<>();
        friendQuery.eq("friend_id",friendId);
        return baseService.getOne(friendQuery);
    }
}
