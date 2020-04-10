package com.zsl.upmall.vo.in.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserBalacneIntegral {
    private Long integral;
    private Long balance;
    private Integer userId;
}
