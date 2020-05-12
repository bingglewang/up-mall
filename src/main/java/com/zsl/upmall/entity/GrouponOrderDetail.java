/**
 * @filename:GrouponOrderDetail 2020年04月08日
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
public class GrouponOrderDetail extends Model<GrouponOrderDetail> {

	private static final long serialVersionUID = 1589254279101L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Integer id;
    
	 /**  */
	private Integer detailId;
    
	 /**  */
	private Integer grouponOrderMasterId;
    
	 /** 参团份额凭证（抽奖图若购买多个，则有多个凭证，此处需分割数据） */
	private String voucher;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
