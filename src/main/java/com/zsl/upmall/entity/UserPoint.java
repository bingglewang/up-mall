/**
 * @filename:UserPoint 2020年04月08日
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
 * @Description:TODO(用户积分明细表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserPoint extends Model<UserPoint> {

	private static final long serialVersionUID = 1586348739294L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Integer id;
    
	 /** 用户id */
	private Integer memberId;
    
	 /** 积分 */
	private BigDecimal pointCount;
    
	 /** 类型(-1：积分消耗；1：积分收入 ;0:积分冻结) */
	private Integer pointType;
    
	 /** 订单号(收入关联订单号或消耗关联订单号) */
	private Integer pointSource;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 创建时间 */
	private Date createTime;
    
	 /** 积分明细项目：1邀请好友购买；2邀请好友成为代理商；3代理商团队拓展；4用户购买商品赠送；5用户购买商品消耗；6用户积分提现消耗 */
	private Integer pointItem;
    
	 /** 积分描述 */
	private String description;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
