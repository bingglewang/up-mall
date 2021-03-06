/**
 * @filename:SkuCustom 2020年04月08日
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
public class SkuCustom extends Model<SkuCustom> {

	private static final long serialVersionUID = 1586571750669L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /**  */
	private Integer skuId;
    
	 /** 套餐类型（预留字段） */
	private Integer comboType;
    
	 /** 套餐唯一标记 */
	private String comboLevel;
    
	 /** 套餐说明 */
	private String comboExplain;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
