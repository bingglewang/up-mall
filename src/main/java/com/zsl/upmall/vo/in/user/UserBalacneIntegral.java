package com.zsl.upmall.vo.in.user;

import lombok.Data;

@Data
public class UserBalacneIntegral {
    /**
     * 扣减得积分
     */
    private Long integral;
    /**
     * 扣减得余额
     */
    private Long balance;
    /**
     * 用户id
     */
    private Integer userId;
}
