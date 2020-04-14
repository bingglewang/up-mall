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

    //sku 详情接口路径
    public final static String SKU_DETAIL_URL = "http://upmall.cntracechain.com/common-goods/common/skuDetail";
    // 扣减库存
    public final static String SKU_SUB_ADD_STOCK = "http://upmall.cntracechain.com/common-goods/common/skuSell";
    // 获取 SKU用户售价
    public final static String SKU_USER_COST_PRICE = "http://upmall.cntracechain.com/common-goods/upMall/userPrice/sku";
    // 获取地址详情
    public final static String ADDRESS_DETAIL = "http://upmall.cntracechain.com/user/userAddress/getById";
    //套餐判断
    public final  static String IS_ORDER_PACKAGE = "http://upmall.cntracechain.com/common-goods/upMall/uncalibrated/customizedCheck";



    public static String getStatusText(Integer status){
        if(status == null){
            return "";
        }
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