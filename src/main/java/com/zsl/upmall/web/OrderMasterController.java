/**
 * @filename:OrderMasterController 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.web;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.aid.PageParam;
import com.zsl.upmall.config.SynQueryDemo;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.*;
import com.zsl.upmall.service.*;
import com.zsl.upmall.task.OrderUnpaidTask;
import com.zsl.upmall.task.TaskService;
import com.zsl.upmall.util.DateUtil;
import com.zsl.upmall.util.HttpClientUtil;
import com.zsl.upmall.vo.in.*;
import com.zsl.upmall.vo.out.Logistics;
import com.zsl.upmall.vo.out.OrderListVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.io.IOUtils;

/**
 * <p>自动生成工具：mybatis-dsc-generator</p>
 * 
 * <p>说明： API接口层</P>
 * @version: V1.0
 * @author: binggleWang
 * @time    2020年04月08日
 *
 */
@RestController
@RequestMapping("/order")
public class OrderMasterController{
    private final Log logger = LogFactory.getLog(OrderMasterController.class);

    @Autowired
    protected OrderMasterService baseService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TrackingService trackingService;

    protected JsonResult result = new JsonResult();


    /**
     * @explain 订单详情
     * @param   id 订单id
     * @return  JsonResult
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @GetMapping("/getById/{id}")
    public JsonResult getById(@PathVariable("id")Long id){
        Map<String, Object> orderInfo = new HashMap<String, Object>();
        OrderMaster orderMaster = baseService.getById(id);
        if(orderMaster == null){
            return result.error("订单不存在");
        }
        orderInfo.put("id", id);
        orderInfo.put("orderSn", orderMaster.getSystemOrderNo());
        orderInfo.put("traceNo",orderMaster.getTransactionOrderNo());
        orderInfo.put("payWay","在线支付");
        orderInfo.put("goodsAmount",orderMaster.getTotalGoodsAmout());
        orderInfo.put("actualPrice",orderMaster.getPracticalPay());
        orderInfo.put("freightPrice",orderMaster.getTotalCarriage());
        orderInfo.put("status",orderMaster.getOrderStatus());
        orderInfo.put("statusText",SystemConfig.getStatusText(orderMaster.getOrderStatus()));
        orderInfo.put("submitTime",DateUtil.DateToString( orderMaster.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
        orderInfo.put("payTime",DateUtil.DateToString( orderMaster.getWaitReceiveTime(),"yyyy-MM-dd HH:mm:ss"));
        orderInfo.put("finishTime",DateUtil.DateToString( orderMaster.getFinishedTime(),"yyyy-MM-dd HH:mm:ss"));
        orderInfo.put("cancelTime",DateUtil.DateToString( orderMaster.getCancelTime(),"yyyy-MM-dd HH:mm:ss"));
        orderInfo.put("expire_time",orderMaster.getCreateTime().getTime() / 1000 + SystemConfig.ORDER_UNPAID / 1000);

        //用户token
        RequestContext requestContext = RequestContextMgr.getLocalContext();

        //订单商品列表
        List<SkuDetailVo> productDetailList = new ArrayList<>();
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",id);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailWrapper);

        for (OrderDetail orderGoods : orderDetails) {
            SkuDetailVo skuDetailVo = HttpClientUtil.getSkuDetailById(orderGoods.getSkuId(),requestContext.getToken());
            skuDetailVo.setProductCount(orderGoods.getGoodsCount());
            productDetailList.add(skuDetailVo);
        }
        orderInfo.put("productDetailList",productDetailList);
        //订单地址信息
        AddressInfo addressInfo = HttpClientUtil.getAddressInfoById(orderMaster.getAddressId(),requestContext.getToken());
        orderInfo.put("addressInfo",addressInfo);
        return result.success(orderInfo);
    }

    /**
     * @explain 下订单
     * @param   orderInfo 订单信息
     * @return  Boolean
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/createOrder")
    public JsonResult createOrder(@Valid @RequestBody CreateOrderVo orderInfo){
        //获取用户 userId
        RequestContext requestContext = RequestContextMgr.getLocalContext();
        Integer userId = requestContext.getUserId();
        if(userId == null){
            return  result.error("用户信息错误");
        }

        //去支付（）
        if(StringUtils.isNotBlank(orderInfo.getOrderSn())){
            QueryWrapper<OrderMaster> toPayOrderQuery = new QueryWrapper<>();
            toPayOrderQuery.eq("system_order_no",orderInfo.getOrderSn()).eq("hidden",0).last("LIMIT 1");
            OrderMaster toPayOrder = baseService.getOne(toPayOrderQuery);
            if(toPayOrder == null){
                return result.error("订单不存在");
            }
            if(toPayOrder.getOrderStatus() - SystemConfig.ORDER_STATUS_WAIT_PAY != 0){
                return result.error("订单状态错误");
            }
            QueryWrapper<OrderDetail> payOrderDetailQuery = new QueryWrapper<>();
            payOrderDetailQuery.eq("order_id",toPayOrder.getId()).last("LIMIT 1");
            OrderDetail payOrderDetail = orderDetailService.getOne(payOrderDetailQuery);
            if(payOrderDetail == null){
                return  result.error("订单信息错误");
            }
            orderInfo.setAddressId(toPayOrder.getAddressId());
            orderInfo.setCartId(0);
            orderInfo.setFreight(toPayOrder.getTotalCarriage());
            orderInfo.setPayWay(toPayOrder.getPayWay());
            orderInfo.setProductCount(payOrderDetail.getGoodsCount());
            orderInfo.setShopId(0);
            orderInfo.setProductId(payOrderDetail.getSkuId());
            orderInfo.setTotalAmount(toPayOrder.getPracticalPay());
            //把之前的订单隐藏
            OrderMaster orderHidden = new OrderMaster();
            orderHidden.setId(toPayOrder.getId());
            orderHidden.setHidden(1);
            baseService.updateById(orderHidden);
        }

        if(orderInfo == null){
            return result.error("订单参数错误");
        }

        if(orderInfo.getCartId() == null || orderInfo.getAddressId() == null){
            return result.error("参数错误");
        }

        // 收货地址
        AddressInfo addressInfo = HttpClientUtil.getAddressInfoById(orderInfo.getAddressId(),requestContext.getToken());
        if (addressInfo == null) {
            return result.error("地址不存在");
        }

        // 商品价格
        List<OrderProductVo> orderProductVoList = new ArrayList<>();
        if (orderInfo.getCartId().equals(0)) {

            // 普通
            SkuDetailVo sku = HttpClientUtil.getSkuDetailById(orderInfo.getProductId(),requestContext.getToken());
            if(sku == null || !sku.getStatus()){
                return result.error("商品不存在或已下架");
            }
            //需要支付得 价格
            BigDecimal needTotalPrice = sku.getSkuPrice().multiply(new BigDecimal(orderInfo.getProductCount())).add(orderInfo.getFreight());
            if(needTotalPrice.compareTo(orderInfo.getTotalAmount()) != 0){
                return result.error("订单价格不一致");
            }
            orderProductVoList.add(new OrderProductVo(sku.getSkuId(),orderInfo.getProductCount(),sku.getSkuPrice(),sku.getSkuImage(),sku.getSpec(),sku.getSkuName()));

        } else {
            // 购物车 (没有 todo)
        }

        //限制每个价格的套餐只能购买一次(说是前端可以限制 todo)

        //创建订单
        Long orderId = null;
        // 最终支付费用
        BigDecimal actualPrice = orderInfo.getTotalAmount();
        OrderMaster order = new OrderMaster();
        order.setAddressId(orderInfo.getAddressId());
        order.setHidden(0);
        order.setMemberId(userId);
        order.setPayWay(orderInfo.getPayWay());
        order.setPracticalPay(actualPrice);
        order.setTotalCarriage(orderInfo.getFreight());
        order.setShopId(0);
        order.setTotalGoodsAmout(orderInfo.getTotalAmount().subtract(orderInfo.getFreight()));
        //订单状态 待付款(状态)
        order.setCreateTime(new Date());
        order.setOrderStatus(SystemConfig.ORDER_STATUS_WAIT_PAY);
        order.setWaitPayTime(new Date());
        //订单号
        order.setSystemOrderNo(generateOrderSn(userId));
        boolean isSaveSuccess = baseService.save(order);
        if(!isSaveSuccess){
            return result.error("下单失败");
        }
        orderId = order.getId();
        // 如果是购物车结算则清除购物车 (todo )

        //商品数量/库存减少
        List<SkuAddStockVo> skuAddStockVos = new ArrayList<>();
        //订单详情
       for(OrderProductVo orderProductVo : orderProductVoList){
           SkuAddStockVo skuAddStockVo = new SkuAddStockVo();
           skuAddStockVo.setCount(orderProductVo.getProductCount());
           skuAddStockVo.setSkuId(orderProductVo.getSkuId());
           skuAddStockVos.add(skuAddStockVo);
           //订单详情
           OrderDetail orderDetail = new OrderDetail();
           orderDetail.setActualCount(orderProductVo.getProductCount());
           orderDetail.setGoodsCount(orderProductVo.getProductCount());
           orderDetail.setGoodsPrice(orderProductVo.getProductPrice());
           orderDetail.setGoodsImg(orderProductVo.getProductImg());
           orderDetail.setGoodsName(orderProductVo.getProductName());
           orderDetail.setGoodsSpec(orderProductVo.getSpec());
           orderDetail.setGoodsCarriage(orderInfo.getFreight());
           orderDetail.setOrderId(orderId);
           orderDetail.setSkuId(orderProductVo.getSkuId());
           orderDetail.setGoodsAmount(orderInfo.getTotalAmount().subtract(orderInfo.getFreight()));
           orderDetail.setPracticalClearing(orderInfo.getTotalAmount());
           orderDetailService.save(orderDetail);
       }

       //下单才扣库存
        if(StringUtils.isBlank(orderInfo.getOrderSn())){
            int addSubStock = HttpClientUtil.skuSubAddStock(skuAddStockVos,requestContext.getToken(),false);
            if(addSubStock - 0 == 0){
                OrderMaster updateHidden = new OrderMaster();
                updateHidden.setId(order.getId());
                updateHidden.setHidden(1);
                baseService.updateById(updateHidden);
                return result.error("扣库存失败");
            }
        }

        // 订单地址处理

        // 订单支付超期任务
        taskService.addTask(new OrderUnpaidTask(orderId));
        Map<String ,Object> map = new HashMap<>();
        map.put("orderId",orderId);
        map.put("orderSn",order.getSystemOrderNo());
        return result.success("下单成功",map);
    }


    /**
     * 生成订单号
     * @param userId
     * @return
     */
    public String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String orderSn = now + getRandomNum(6);
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = now + getRandomNum(6);
        }
        return orderSn;
    }

    public int countByOrderSn(Integer userId, String orderSn) {
        QueryWrapper<OrderMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id",userId).eq("system_order_no",orderSn);
        return baseService.count(queryWrapper);
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * @explain 订单列表
     * @param   param ,others,对象参数：AppPage<UserFriend>
     * @return  PageInfo<UserFriend>
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @GetMapping("/getOrderPages")
    public JsonResult<IPage<OrderListVo>> getPages(PageParam param, Integer orderStatus){
        JsonResult<IPage<OrderListVo>> returnPage = new JsonResult<IPage<OrderListVo>>();
        //获取用户 userId
        RequestContext requestContext = RequestContextMgr.getLocalContext();
        Integer userId = requestContext.getUserId();
        Page<OrderListVo> page=new Page(param.getPageNum(),param.getPageSize());
        if(orderStatus == null){
            //查询全部
            orderStatus = -1;
        }
        if((orderStatus - SystemConfig.ORDER_STATUS_RECIEVE != 0) && (orderStatus - SystemConfig.ORDER_STATUS_WAIT_PAY != 0) && (orderStatus - SystemConfig.ORDER_STATUS_CANCLE != 0)  && (orderStatus - SystemConfig.ORDER_STATUS_FINISH != 0)){
            //查询全部
            orderStatus = -1;
        }
        //分页数据
        IPage<OrderListVo> pageData = baseService.getOrderListByStatus(page, orderStatus,userId);
        returnPage.success(pageData);

        return returnPage;
    }


    /**
     *  确认收货
     * @param id 订单id
     * @return 取消订单操作结果
     */
    @PostMapping("/updateOrderStatus/{id}")
    public JsonResult<IPage> updateOrderStatus(@PathVariable("id")Long id) {
        if (id == null) {
            return result.error("参数错误");
        }
        OrderMaster orderMaster = baseService.getById(id);
        if(orderMaster == null){
            return result.error("订单不存在");
        }
        //判断订单状态是否为 待收货
        if(orderMaster.getOrderStatus() - SystemConfig.ORDER_STATUS_RECIEVE != 0){
            return result.error("订单状态不对");
        }

        // 设置订单已完成
        OrderMaster updateStatus = new OrderMaster();
        updateStatus.setId(orderMaster.getId());
        updateStatus.setOrderStatus(SystemConfig.ORDER_STATUS_FINISH );
        updateStatus.setFinishedTime(new Date());
        if (!baseService.updateById(updateStatus)) {
            throw new RuntimeException("确认收货失败");
        }
        // todo 用户冻结积分，冻结余额操作
        return result.success("确认收货成功");
    }


    /**
     * 取消订单
     * 1. 检测当前订单是否能够取消；
     * 2. 设置订单取消状态；
     * 3. 商品货品库存恢复；
     * @param id 订单id
     * @return 取消订单操作结果
     */
    @PostMapping("/cancel/{id}")
    public JsonResult<IPage> cancel(@PathVariable("id")Long id) {
        if (id == null) {
            return result.error("参数错误");
        }

        OrderMaster orderMaster = baseService.getById(id);
        if(orderMaster == null){
            return result.error("订单不存在");
        }
        //判断订单状态是否为待付款
        if(orderMaster.getOrderStatus() - SystemConfig.ORDER_STATUS_WAIT_PAY != 0){
            return result.error("订单状态不对");
        }

        // 设置订单已取消状态
        OrderMaster upOrderCancel = new OrderMaster();
        upOrderCancel.setId(orderMaster.getId());
        upOrderCancel.setOrderStatus(SystemConfig.ORDER_STATUS_CANCLE );
        upOrderCancel.setCancelTime(new Date());
        if (!baseService.updateById(upOrderCancel)) {
            throw new RuntimeException("更新数据已失效");
        }

        RequestContext requestContext = RequestContextMgr.getLocalContext();
        // 商品货品数量增加
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",id);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailWrapper);

        //商品数量/库存减少
        List<SkuAddStockVo> skuAddStockVos = new ArrayList<>();
        for(OrderDetail orderGoods : orderDetails){
            SkuAddStockVo skuAddStockVo = new SkuAddStockVo();
            skuAddStockVo.setCount(orderGoods.getGoodsCount());
            skuAddStockVo.setSkuId(orderGoods.getSkuId());
            skuAddStockVos.add(skuAddStockVo);
        }
        int addSubStock = HttpClientUtil.skuSubAddStock(skuAddStockVos,requestContext.getToken(),true);
        if(addSubStock - 0 == 0){
            return result.error("扣库存失败");
        }

        return result.success("修改成功");
    }


    /**
     * 微信付款成功或失败回调接口
     * 1. 检测当前订单是否是付款状态;
     * 2. 设置订单付款成功状态相关信息;
     * 3. 响应微信商户平台.
     * @param request  请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @PostMapping("pay-notify")
    public Object payNotify(HttpServletRequest request, HttpServletResponse response){
        String xmlResult = null;
        try {
            xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        } catch (IOException e) {
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }


        logger.info("处理腾讯支付平台的订单支付");
        logger.info(result);

        String orderSn = "";
        String outTradeNo = "";

        // 分转化成元
        String totalFee = BaseWxPayResult.fenToYuan(0);
        QueryWrapper<OrderMaster> orderQuery = new QueryWrapper<>();
        orderQuery.eq("system_order_no",orderSn).eq("hidden",0).last("LIMIT 1");
        OrderMaster order = baseService.getOne(orderQuery);
        if (order == null) {
            return WxPayNotifyResponse.fail("订单不存在 sn=" + orderSn);
        }

        // 检查这个订单是否已经处理过
        if (order.getOrderStatus() - SystemConfig.ORDER_STATUS_WAIT_PAY != 0) {
            return WxPayNotifyResponse.success("订单已经处理成功!");
        }

        // 检查支付订单金额
        if (!totalFee.equals(order.getPracticalPay().toString())) {
            return WxPayNotifyResponse.fail(order.getSystemOrderNo() + " : 支付金额不符合 totalFee=" + totalFee);
        }

        // 设置订单 待收货
        OrderMaster upOrderReceived = new OrderMaster();
        upOrderReceived.setId(order.getId());
        upOrderReceived.setOrderStatus(SystemConfig.ORDER_STATUS_RECIEVE );
        upOrderReceived.setWaitReceiveTime(new Date());
        upOrderReceived.setTransactionOrderNo(outTradeNo);
        upOrderReceived.setPayTime(new Date());
        if (!baseService.updateById(upOrderReceived)) {
            throw new RuntimeException("更新数据已失效");
        }

        // 取消订单超时未支付任务
        taskService.removeTask(new OrderUnpaidTask(order.getId()));
        return WxPayNotifyResponse.success("处理成功!");
    }


    /**
     * 按orderSn查询OrderDetail对象（orderSn）
     * @param orderSn 订单号
     * @return
     */
    @GetMapping("getOrderDetailByOrderId/{id}")
    public JsonResult getOrderDetailByOrderId(@PathVariable("id") String orderSn){
        QueryWrapper<OrderMaster> OrderMasterWrapper = new QueryWrapper();
        OrderMasterWrapper.eq("system_order_no",orderSn).eq("hidden",0).last("LIMIT 1");
        OrderMaster orderMaster = baseService.getOne(OrderMasterWrapper);
        if(orderMaster == null ){
            return result.error("订单不存在");
        }
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",orderMaster.getId());
       return result.success(orderDetailService.list(orderDetailWrapper));
    }

    /**
     * 用户id查询OrderMaster信息（memberId）
     * @param userId 用户id
     * @return
     */
    @GetMapping("getOrderByUserId/{id}")
    public JsonResult getOrderByUserId(@PathVariable("id") Integer userId){
        QueryWrapper<OrderMaster> orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("member_id",userId).eq("hidden",0);
        return result.success(baseService.list(orderDetailWrapper));
    }


    /**
     * 判断该订单有没有支付（）（订单号, 套餐唯一标识）
     * @param orderSn  订单号
     * @param sign 套餐唯一标识
     * @return
     */
    @GetMapping("isBuyPackage")
    public JsonResult isBuyPackage(String orderSn,String sign) {
        QueryWrapper<OrderMaster> orderMasterQueryWrapper = new QueryWrapper();
        orderMasterQueryWrapper.eq("system_order_no",orderSn).eq("hidden",0).last("LIMIT 1");
        OrderMaster orderMaster = baseService.getOne(orderMasterQueryWrapper);
        if(orderMaster == null){
            return result.error("订单不存在",false);
        }
        if(orderMaster.getOrderStatus() - SystemConfig.ORDER_STATUS_WAIT_PAY == 0){
            return result.error("订单没有支付",false);
        }else if(orderMaster.getOrderStatus() - SystemConfig.ORDER_STATUS_CANCLE == 0){
            return result.error("订单已取消",false);
        }else{
            //判断订单详情sku数量是否为1
            QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper();
            orderDetailQueryWrapper.eq("order_id",orderMaster.getId()).last("LIMIT 1");
            OrderDetail orderDetail = orderDetailService.getOne(orderDetailQueryWrapper);
            if(orderDetail == null){
                return result.error("订单错误",false);
            }else{
                if(orderDetail.getGoodsCount() - 1 == 0){
                    return result.success( orderDetail.getGoodsPrice() + "",HttpClientUtil.isPackage(orderDetail.getSkuId(),sign));
                }
            }
            return result.success("",false);
        }
    }

    /**
     * 查看物流
     * @param orderId
     * @return
     */
    @GetMapping("getTracking/{id}")
    public JsonResult isBuyPackage(@PathVariable("id") Integer orderId) {
        OrderMaster orderMaster = baseService.getById(orderId);
        if(orderMaster == null || orderMaster.getTrackingCompanyId() == null || StringUtils.isBlank(orderMaster.getTrackingNumber())){
            return result.error("物流信息为空");
        }

        Tracking tracking = trackingService.getById(orderMaster.getTrackingCompanyId());
        if(tracking == null){
            return result.error("不支持该物流公司");
        }

        String resultTracking = new SynQueryDemo().synQueryData(tracking.getTrackingCode(), orderMaster.getTrackingNumber(), "", "", "");
        if(StringUtils.isNotBlank(resultTracking)){
            try{
                Logistics logistics = JSON.parseObject(resultTracking,Logistics.class);
                if(logistics != null && logistics.getStatus() - 200 == 0){
                    Map<String ,Object> map = new HashMap<>();
                    map.put("trackingName",tracking.getTrackingCompanyName());
                    map.put("trackingNum",orderMaster.getTrackingNumber());
                    map.put("trackList",logistics.getData());
                    return result.success(map);
                }else{
                    if(logistics == null){
                        return result.error("物流信息为空");
                    }
                    return result.error(logistics.getMessage());
                }
            }catch (Exception e){
                return result.error("获取物流失败");
            }
        }else{
            return result.error("获取物流失败");
        }
    }
}