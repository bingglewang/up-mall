/**
 * @filename:SpuTag 2020年04月08日
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
 * @Description:TODO(商品标签中间表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpuTag extends Model<SpuTag> {

	private static final long serialVersionUID = 1586348738458L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** 自增ID */
	private Integer id;
    
	 /** 标签ID */
	private Integer tagId;
    
	 /** spu_id */
	private Integer spuId;
    
	 /** 应用状态（0：停用，1：启用） */
	private Integer useStatus;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
