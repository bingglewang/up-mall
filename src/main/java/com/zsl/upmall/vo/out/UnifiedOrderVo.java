package com.zsl.upmall.vo.out;

import lombok.Data;

@Data
public class UnifiedOrderVo {
   private Integer statusCode;
   private String statusMsg;
   private UnifiedData data;
}



@Data
class UnifiedData{
    private String nonce_str;
    private String appid;
    private String sign;
    private String err_code;
    private String return_msg;
    private String result_code;
    private String err_code_des;
    private String mch_id;
    private String return_code;
}