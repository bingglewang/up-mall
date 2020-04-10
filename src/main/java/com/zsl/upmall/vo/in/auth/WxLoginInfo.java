package com.zsl.upmall.vo.in.auth;

import com.zsl.upmall.entity.UserMember;
import lombok.Data;

@Data
public class WxLoginInfo {
    /**
     * 微信登录code
     */
    private String code;

    /**
     * 用户信息
     */
    private UserMember userMember;
}
