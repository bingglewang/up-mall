/**
 * @filename:GrouponOrderMaster 2020年04月08日
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
public class GrouponOrderMaster extends Model<GrouponOrderMaster> {

	private static final long serialVersionUID = 1589254279304L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Integer id;
    
	 /** 开团订单ID */
	private Integer grouponOrderId;
    
	 /** 订单id */
	private Integer orderId;
    
	 /** 拼团结果 */
	private String grouponResult;
    
	 /** 返还奖励金 */
	private BigDecimal backPrize;
    
	 /** 拼团状态（0拼团中，1拼团成功，2拼团失败） */
	private Integer grouponStatus;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
