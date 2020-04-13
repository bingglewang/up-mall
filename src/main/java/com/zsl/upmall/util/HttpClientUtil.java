package com.zsl.upmall.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zsl.upmall.config.SystemConfig;
import com.zsl.upmall.vo.in.*;
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
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

    public static String doGet(String url, Map<String, String> param) {

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

    public static String doGet(String url) {
        return doGet(url, null);
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
        System.out.println("sku详情获取结果--->"+skuResult);
        SkuDetailVo result = null;
        try {
            if(StringUtils.isNotBlank(skuResult)){
                SkuResult skuObject = JSON.parseObject(skuResult,SkuResult.class);
                if(skuObject != null && skuObject.getCode() - 200200 == 0){
                    result = skuObject.getData();
                    String skuPriceResult =  HttpClientUtil.doGet(SystemConfig.SKU_USER_COST_PRICE+"/"+skuId+"&token="+token);
                    if(StringUtils.isNotBlank(skuPriceResult)){
                        CommonGoodsResult skuPrice =  JSON.parseObject(skuPriceResult,CommonGoodsResult.class);
                        if(skuPrice != null && skuPrice.getCode() - 200200 == 0){
                            result.setSkuPrice(new BigDecimal(skuPrice.getData().toString()));
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


    public static void main(String[] args) {
          /*SkuDetailVo skuDetailVo = getSkuDetailById(23);
        System.out.println("我的封装结果："+skuDetailVo);*/
       /* SkuAddStockVo skuAddStockVo = new SkuAddStockVo();
        skuAddStockVo.setCount(2);
        skuAddStockVo.setSkuId(1);
        List<SkuAddStockVo> skuAddStockVos = new ArrayList<>();
        skuAddStockVos.add(skuAddStockVo);
        int i = skuSubAddStock(skuAddStockVos,"",false);
        System.out.println("我的封装结果："+ i);*/
      /* AddressInfo addressInfo = getAddressInfoById(5,"92141c1b-db5c-4082-bd36-7cbdf19aa159");
        System.out.println("地址详情："+ addressInfo);*/
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

