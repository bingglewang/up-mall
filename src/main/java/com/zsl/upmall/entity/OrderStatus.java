/**
 * @filename:OrderStatus 2020年04月08日
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
 * @Description:TODO(实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderStatus extends Model<OrderStatus> {

	private static final long serialVersionUID = 1586348736830L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Long id;
    
	 /** 订单号 */
	private Long orderId;
    
	 /** 订单状态(0:待付款，1：待收货，2：已完成，3：已取消) */
	private Integer orderStatus;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 创建时间 */
	private Date createTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
