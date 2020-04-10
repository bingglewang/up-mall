/**
 * @filename:OrderMasterController 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.aid.PageParam;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.*;
import com.zsl.upmall.service.*;
import com.zsl.upmall.task.OrderUnpaidTask;
import com.zsl.upmall.task.TaskService;
import com.zsl.upmall.util.DateUtil;
import com.zsl.upmall.vo.in.order.CreateOrderVo;
import com.zsl.upmall.vo.in.order.OrderProductVo;
import com.zsl.upmall.vo.out.address.AddressInfo;
import com.zsl.upmall.vo.out.order.OrderListVo;
import com.zsl.upmall.vo.out.product.OrderListProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired
    protected OrderMasterService baseService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private UserAddressService addressService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ShoppingCartService shoppingCartService;

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
        //判断订单状态是否为待付款
        QueryWrapper<OrderStatus> orderStatusWarpper = new QueryWrapper();
        orderStatusWarpper.eq("order_id",id);
        OrderStatus orderStatus = orderStatusService.getOne(orderStatusWarpper);
        if(orderStatus == null){
            return result.error("订单状态不对");
        }
        orderInfo.put("status",orderStatus.getOrderStatus());
        orderInfo.put("statusText",SystemConfig.getStatusText(orderStatus.getOrderStatus()));
        orderInfo.put("submitTime",DateUtil.DateToString( orderStatus.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));

        List<OrderListProductVo> productDetailList = new ArrayList<>();
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",id);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailWrapper);

        for (OrderDetail orderGoods : orderDetails) {
            Sku  skuDetail  = skuService.getById(orderGoods.getSkuId());
            OrderListProductVo orderListProductVo = new OrderListProductVo();
            orderListProductVo.setProductCount(orderGoods.getGoodsCount());
            orderListProductVo.setProductImg(skuDetail.getSkuPicture());
            orderListProductVo.setProductName(skuDetail.getSkuName());
            orderListProductVo.setProductPrice(skuDetail.getRetailPrice());
            productDetailList.add(orderListProductVo);
        }
        orderInfo.put("productDetailList",productDetailList);
        AddressInfo addressInfo = addressService.addressInfo(new Long(orderMaster.getAddressId()));
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

        if(orderInfo == null){
            return result.error("订单参数错误");
        }

        if(orderInfo.getCartId() == null || orderInfo.getAddressId() == null){
            return result.error("参数错误");
        }

        // 收货地址
        UserAddress checkedAddress = userAddressService.getById(orderInfo.getAddressId());
        if (checkedAddress == null) {
            return result.error("地址不存在");
        }

        // 商品价格
        List<OrderProductVo> orderProductVoList = new ArrayList<>();
        if (orderInfo.getCartId().equals(0)) {
            // 普通
            Sku sku = skuService.getById(orderInfo.getProductId());
            if(sku == null || (sku.getStatus()-1 != 0)){
                return result.error("商品不存在或已下架");
            }
            //需要支付得 价格
            BigDecimal needTotalPrice = sku.getRetailPrice().multiply(new BigDecimal(orderInfo.getProductCount())).add(orderInfo.getFreight());
            if(needTotalPrice.compareTo(orderInfo.getTotalAmount()) != 0){
                return result.error("订单价格不一致");
            }
            orderProductVoList.add(new OrderProductVo(sku.getId(),orderInfo.getProductCount(),sku.getRetailPrice()));

        } else {
            // 购物车
            QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
            if(shoppingCarts.isEmpty()){
                return  result.error("购物车已经清空");
            }
            BigDecimal needTotalCartPrice = new BigDecimal(0);
            for(ShoppingCart cart : shoppingCarts){
                BigDecimal itemPrice = cart.getAddedPrice().multiply(new BigDecimal(cart.getGoodsNum()));
                needTotalCartPrice = needTotalCartPrice.add(itemPrice);
                orderProductVoList.add(new OrderProductVo(cart.getSkuId(),cart.getGoodsNum(),cart.getAddedPrice()));
            }
            needTotalCartPrice.add(orderInfo.getFreight());
            if(needTotalCartPrice.compareTo(orderInfo.getTotalAmount()) != 0){
                return result.error("订单价格不一致");
            }
            if(orderProductVoList.isEmpty()){
                return result.error("请选择需要购买得商品");
            }
        }

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
        //订单号
        order.setSystemOrderNo(generateOrderSn(userId));
        boolean isSaveSuccess = baseService.save(order);
        if(!isSaveSuccess){
            return result.error("下单失败");
        }
        orderId = order.getId();
        // 如果是购物车结算则清除购物车
        QueryWrapper<ShoppingCart> removeWrapper = new QueryWrapper<>();
        removeWrapper.eq("user_id",userId);
        shoppingCartService.remove(removeWrapper);
        //订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(new Date());
        orderStatus.setOrderId(orderId);
        //待付款(状态)
        orderStatus.setOrderStatus(SystemConfig.ORDER_STATUS_WAIT_PAY);
        orderStatusService.save(orderStatus);
        //订单详情
       for(OrderProductVo orderProductVo : orderProductVoList){
           //商品数量/库存减少
           Sku skuDetail = skuService.getById(orderProductVo.getSkuId());
           Sku productCountReduce = new Sku();
           productCountReduce.setId(skuDetail.getId());
           productCountReduce.setStock(skuDetail.getStock() - orderProductVo.getProductCount());
           skuService.updateById(productCountReduce);
           //订单详情
           OrderDetail orderDetail = new OrderDetail();
           orderDetail.setActualCount(orderProductVo.getProductCount());
           orderDetail.setGoodsCount(orderProductVo.getProductCount());
           orderDetail.setGoodsPrice(orderProductVo.getProductPrice());
           orderDetail.setGoodsCarriage(orderInfo.getFreight());
           orderDetail.setOrderId(orderId);
           orderDetail.setSkuId(orderProductVo.getSkuId());
           orderDetail.setGoodsAmount(orderInfo.getTotalAmount().subtract(orderInfo.getFreight()));
           orderDetail.setPracticalClearing(orderInfo.getTotalAmount());
           orderDetailService.save(orderDetail);
       }
        // 订单支付超期任务
        taskService.addTask(new OrderUnpaidTask(orderId));

        return result.success("下单成功",orderId);
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
        //判断订单状态是否为待付款
        QueryWrapper<OrderStatus> orderStatusWarpper = new QueryWrapper();
        orderStatusWarpper.eq("order_id",id);
        OrderStatus orderStatus = orderStatusService.getOne(orderStatusWarpper);
        if(orderStatus == null || (orderStatus.getOrderStatus() - SystemConfig.ORDER_STATUS_RECIEVE != 0)){
            return result.error("订单状态不对");
        }

        // 设置订单已完成
        OrderStatus setOrderStatus = new OrderStatus();
        setOrderStatus.setId(orderStatus.getId());
        setOrderStatus.setOrderStatus(SystemConfig.ORDER_STATUS_FINISH );
        if (!orderStatusService.updateById(setOrderStatus)) {
            throw new RuntimeException("确认收货失败");
        }
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
        //判断订单状态是否为待付款
        QueryWrapper<OrderStatus> orderStatusWarpper = new QueryWrapper();
        orderStatusWarpper.eq("order_id",id);
        OrderStatus orderStatus = orderStatusService.getOne(orderStatusWarpper);
        if(orderStatus == null || (orderStatus.getOrderStatus() - SystemConfig.ORDER_STATUS_WAIT_PAY != 0)){
            return result.error("订单不存在或状态不对");
        }

        // 设置订单已取消状态
        OrderStatus setOrderStatus = new OrderStatus();
        setOrderStatus.setId(orderStatus.getId());
        setOrderStatus.setOrderStatus(SystemConfig.ORDER_STATUS_CANCLE );
        if (!orderStatusService.updateById(setOrderStatus)) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",id);
        List<OrderDetail> orderDetails = orderDetailService.list(orderDetailWrapper);

        for (OrderDetail orderGoods : orderDetails) {
            Sku  skuDetail  = skuService.getById(orderGoods.getSkuId());
            Sku addStock = new Sku();
            addStock.setId(skuDetail.getId());
            addStock.setStock(skuDetail.getStock() + orderGoods.getGoodsCount());
            if ( !skuService.updateById(addStock)) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }
        return result.success("修改成功");
    }

    // todo 确认付款 (根据订单号)
    // todo 付款后 改变张订单状态


    /**
     * 按orderId查询OrderDetail对象（orderId）
     * @param orderId 订单id
     * @return
     */
    @GetMapping("getOrderDetailByOrderId/{id}")
    public JsonResult getOrderDetailByOrderId(@PathVariable("id") Integer orderId){
        QueryWrapper orderDetailWrapper = new QueryWrapper();
        orderDetailWrapper.eq("order_id",orderId);
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




}