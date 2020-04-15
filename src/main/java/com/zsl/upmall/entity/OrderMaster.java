/**
 * @filename:OrderMaster 2020年04月08日
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
 * @Description:订单(实体类)
 *
 * @version: V1.0
 * @author: binggleWang
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderMaster extends Model<OrderMaster> {

	private static final long serialVersionUID = 1586571750011L;

	@TableId(value = "id", type = IdType.AUTO)
	/** id主键 */
	private Long id;

	/** 系统订单号 */
	private String systemOrderNo;

	/** 交易流水号 */
	private String transactionOrderNo;

	/** 支付方式 */
	private Integer payWay;

	/** 用户id */
	private Integer memberId;

	/** 配送地址（用户如修改已下单的地址，原地址置为删除状态，再新增新地址） */
	private Integer addressId;

	/** 商家id */
	private Integer shopId;

	/** 商品总金额 */
	private BigDecimal totalGoodsAmout;

	/** 商品总运费 */
	private BigDecimal totalCarriage;

	/** 实际支付金额 */
	private BigDecimal practicalPay;

	/** 是否删除（0否1是） */
	private Integer hidden;

	/** 备注 */
	private String remark;

	/** 物流公司id */
	private Integer trackingCompanyId;

	/** 物流单号 */
	private String trackingNumber;

	/** 订单状态（0待付款，1待收货，2已完成，3已取消） */
	private Integer orderStatus;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	/** 创建时间 */
	private Date createTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	/** 待支付时间 */
	private Date waitPayTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	/** 支付时间 */
	private Date payTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	/** 待收货时间 */
	private Date waitReceiveTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	/** 完成时间 */
	private Date finishedTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	/** 取消时间 */
	private Date cancelTime;


	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}
