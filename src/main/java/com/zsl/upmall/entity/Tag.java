/**
 * @filename:Tag 2020年04月08日
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
 * @Description:TODO(商品标签实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Tag extends Model<Tag> {

	private static final long serialVersionUID = 1586348738654L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** 自增ID */
	private Integer id;
    
	 /** 标签 */
	private String tag;
    
	 /** 标签状态（0：停用，1：启用） */
	private Integer tagStatus;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
