package com.zsl.upmall.config;

/**
 * 系统设置
 */
public class SystemConfig {
    // 订单超时取消 （毫秒）
    public final static Integer ORDER_UNPAID = 30 * 60 * 1000;
    // 订单状态（ 待付款）
    public final static Integer ORDER_STATUS_WAIT_PAY = 0;
    // 订单状态（ 待收货）
    public final static Integer ORDER_STATUS_RECIEVE = 1;
    // 订单状态（ 已完成）
    public final static Integer ORDER_STATUS_FINISH = 2;
    // 订单状态（ 已取消）
    public final static Integer ORDER_STATUS_CANCLE = 3;



    public static String getStatusText(Integer status){
        if(status - ORDER_STATUS_WAIT_PAY == 0){
            return "待付款";
        }else if(status - ORDER_STATUS_RECIEVE == 0){
            return "待收货";
        }else if(status - ORDER_STATUS_FINISH == 0){
            return "已完成";
        }else if(status - ORDER_STATUS_CANCLE == 0){
            return "已取消";
        }else {
            return "";
        }
    }

}