/**
 * @filename:UserBalance 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd. 
 * All right reserved. 
 */
package com.zsl.upmall.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**   
 * @Description:TODO(用户余额明细表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBalance extends Model<UserBalance> {

	private static final long serialVersionUID = 1586348738868L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Integer id;
    
	 /** 用户id */
	private Integer memberId;
    
	 /** 余额 */
	private BigDecimal balanceCount;
    
	 /** 余额类型(-1：余额消耗；1：余额收入；0：余额冻结 ) */
	private Integer balanceType;
    
	 /** 关联订单号/退款订单号 */
	private String balanceSource;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 创建时间 */
	private Date createTime;
    
	 /** 订单类型(0：节点计算，1：订单商品赠送，2：退款售后扣除赠送余额，3：支付抵扣，4：退款售后退还金额) */
	private Integer orderType;
    
	 /** 描述 */
	private String description;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
