package com.zsl.upmall.vo;

import com.zsl.upmall.entity.GrouponOrderMaster;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BalanceRefundListVo
 * @Description TODO
 * @Author binggleW
 * @Date 2020-05-18 18:45
 * @Version 1.0
 **/
@Data
public class BalanceRefundListVo {
    private List<GrouponOrderMaster> balanceList;
}
