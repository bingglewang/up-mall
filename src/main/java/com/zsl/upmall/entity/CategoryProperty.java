/**
 * @filename:CategoryProperty 2020年04月08日
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
public class CategoryProperty extends Model<CategoryProperty> {

	private static final long serialVersionUID = 1586348736195L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /**  */
	private Integer categoryId;
    
	 /**  */
	private Integer propertyId;
    
	 /** 录入类型：0文本框 1下拉框 */
	private Integer enterType;
    
	 /** 字段长度 */
	private Integer fieldLength;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
