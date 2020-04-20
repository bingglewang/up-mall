package com.zsl.upmall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.vo.in.*;
import com.zsl.upmall.vo.out.UnifiedOrderVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    public static String doGet(String url, Map<String, String> param,String token) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("token",token);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    public static String doGetToken(String url,String token) {
        return doGet(url, null,token);
    }

    public static String doGet(String url) {
        return doGet(url, null,"");
    }

    public static String doPost(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public static String doPost(String url) {
        return doPost(url, null);
    }


    /**
     * 获取sku详情
     * @param skuId
     * @return
     */
    public static SkuDetailVo getSkuDetailById(Integer skuId,String token){
        //获取sku 详情
        String skuResult =  HttpClientUtil.doGet(SystemConfig.SKU_DETAIL_URL+"?system=UPMALL&skuId="+skuId);
        SkuDetailVo result = null;
        try {
            if(StringUtils.isNotBlank(skuResult)){
                SkuResult skuObject = JSON.parseObject(skuResult,SkuResult.class);
                if(skuObject != null && skuObject.getCode() - 200200 == 0){
                    result = skuObject.getData();
                    String skuPriceResult =  HttpClientUtil.doGetToken(SystemConfig.SKU_USER_COST_PRICE+"/"+skuId,token);
                    if(StringUtils.isNotBlank(skuPriceResult)){
                        CommonGoodsResult skuPrice =  JSON.parseObject(skuPriceResult,CommonGoodsResult.class);
                        if(skuPrice != null && skuPrice.getCode() - 200200 == 0){
                            result.setSkuPrice(new BigDecimal(skuPrice.getData().toString()));
                            System.out.println("sku详情获取结果--->"+skuResult);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * sku 扣减库存
     */
    public static int skuSubAddStock(List<SkuAddStockVo> skuAddStockVos, String token,boolean action){
        JSONObject params = new JSONObject();
        params.put("system","UPMALL");
        params.put("detail",skuAddStockVos);
        params.put("action",action);
        String result = doPostJson(SystemConfig.SKU_SUB_ADD_STOCK,params.toJSONString(),token);
        int i = 0;
        try {
            if(StringUtils.isNotBlank(result)){
                CommonGoodsResult commonGoodsResult = JSON.parseObject(result,CommonGoodsResult.class);
                if(commonGoodsResult != null && commonGoodsResult.getCode() - 200200 == 0){
                    i = 1;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 获取地址详情
     * @param addressId
     * @return
     */
    public static AddressInfo getAddressInfoById(Integer addressId,String token){
        String result = doGet(SystemConfig.ADDRESS_DETAIL+"/"+addressId+"?token="+token);
        AddressInfo addressInfo = null;
        try {
            if(StringUtils.isNotBlank(result)){
                AddressResultVo addressResultVo = JSON.parseObject(result,AddressResultVo.class);
                if(addressResultVo != null){
                   addressInfo = addressResultVo.getData();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return addressInfo;
    }

    /**
     * 判断是否为套餐
     * @param skuId
     * @return
     */
    public static boolean isPackage(Integer skuId,String sign){
        String result = doGet(SystemConfig.IS_ORDER_PACKAGE+"?skuId="+skuId+"&sign="+sign);
        boolean isPack = false;
        try {
            if(StringUtils.isNotBlank(result)){
                CommonGoodsResult addressResultVo = JSON.parseObject(result,CommonGoodsResult.class);
                if(addressResultVo != null){
                    isPack = (boolean)addressResultVo.getData();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isPack;
    }


    /**
     * 调用微信统一下单接口
     * @param ip
     * @param openId
     * @param body
     * @param orderSn
     * @param totalFeel
     * @return
     */
    public static UnifiedOrderVo unifiedOrder(String ip, String openId, String body, String orderSn, String totalFeel){
        JSONObject params = new JSONObject();
        params.put("source",SystemConfig.SYSTEM_UNIQUE_CODE);
        params.put("trade_type","MINIAPP");
        params.put("openid",openId);
        params.put("body",body);
        params.put("out_trade_no",orderSn);
        params.put("total_fee",totalFeel);
        params.put("spbill_create_ip",ip);
        params.put("business_notify_url",SystemConfig.BUSINESS_NOTIFY_URL);
        String result = doPostJson(SystemConfig.WEIXIN_UNION_RUL,params.toJSONString(),"");
        UnifiedOrderVo unifiedResult = null;
        try {
            if(StringUtils.isNotBlank(result)){
                UnifiedOrderVo unifiedOrderVo = JSON.parseObject(result,UnifiedOrderVo.class);
                if(unifiedOrderVo != null && unifiedOrderVo.getStatusCode() - 200 == 0){
                    unifiedResult = unifiedOrderVo;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return unifiedResult;
    }


    /**
     * 将地址设置成假删除，并且新增一条
     * @param addressId
     * @return
     */
    public static int updateAddressAndAdd(Integer addressId,String token){
        String result = doPostJson(SystemConfig.ADDRESS_DELETE_ADD+"/"+addressId,"",token);
        int i = 0;
        try {
            if(StringUtils.isNotBlank(result)){
                AddressResultVo addressResultVo = JSON.parseObject(result,AddressResultVo.class);
                if(addressResultVo != null && "100200".equals(addressResultVo.getCode())){
                    i = 1;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return i;
    }


    /**
     * 会员邀请及普通消费返利
     * @param memberId
     * @param orderNo
     * @return
     */
    public static InviteRebateVo inviteRebate(Integer memberId,String orderNo,String token){
        JSONObject params = new JSONObject();
        params.put("memberId",memberId);
        params.put("orderNo",orderNo);
        String result = doPostJson(SystemConfig.MEMBER_INVITEREBATE_URL,params.toJSONString(),token);
        InviteRebateVo inviteResult = null;
        try {
            if(StringUtils.isNotBlank(result)){
                InviteRebateVo inviteRebateVo = JSON.parseObject(result,InviteRebateVo.class);
                if(inviteRebateVo != null &&  inviteRebateVo.getCode() - 500200 == 0){
                    inviteResult = inviteRebateVo;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return inviteResult;
    }


    /**
     * 代理商绑定
     * @param userId
     * @param shareId
     * @return
     */
    public static int agentShareBind(Integer userId,String shareId){
        String result = doGet(SystemConfig.AGENT_SHARE_BIND+"?userId="+userId+"&shareId="+shareId);
        int isPack = 0;
        try {
            if(StringUtils.isNotBlank(result)){
                AddressResultVo addressResultVo = JSON.parseObject(result,AddressResultVo.class);
                if(addressResultVo != null && "100200".equals(addressResultVo.getCode())){
                    isPack = 1;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isPack;
    }


    public static void main(String[] args) {
      /*  InviteRebateVo inviteRebateVo =  inviteRebate(5,"20200416108383");*/
        //System.out.println("会员返利结果："+inviteRebateVo);
       /*   SkuDetailVo skuDetailVo = getSkuDetailById(23);
        System.out.println("我的封装结果："+skuDetailVo);*/
       /* SkuAddStockVo skuAddStockVo = new SkuAddStockVo();
        skuAddStockVo.setCount(2);
        skuAddStockVo.setSkuId(28);
        List<SkuAddStockVo> skuAddStockVos = new ArrayList<>();
        skuAddStockVos.add(skuAddStockVo);
        int i = skuSubAddStock(skuAddStockVos,"",false);
        System.out.println("我的封装结果："+ i);*/
      /* AddressInfo addressInfo = getAddressInfoById(5,"92141c1b-db5c-4082-bd36-7cbdf19aa159");
        System.out.println("地址详情："+ addressInfo);*/
       /* System.out.println("套餐判断结果："+ isPackage(46,"AA"));*/
      /*  int i = updateAddressAndAdd(67,"be7614ac-08c1-42e0-bae4-73e20c7a4724");
        System.out.println("地址结果："+i);*/
        int i = agentShareBind(5,"RNh7kB");
        System.out.println("地址结果："+i);
    }


    public static String doPostJson(String url, String json,String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            httpPost.setHeader("token",token);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultString;
    }
}

