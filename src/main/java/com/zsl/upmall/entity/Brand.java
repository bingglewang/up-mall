/**
 * @filename:Brand 2020年04月08日
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
 * @Description:TODO(品牌表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Brand extends Model<Brand> {

	private static final long serialVersionUID = 1586348735852L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Long id;
    
	 /** 公司ID */
	private Long companyId;
    
	 /** 品牌名称 */
	private String brandName;
    
	 /** 品牌首字母 */
	private String initialLetter;
    
	 /** 品牌描述 */
	private String brandDescription;
    
	 /** 品牌logo */
	private String brandLogo;
    
	 /** 状态：0：终止合作；1：正常合作；2：暂停合作 */
	private Integer status;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 创建时间 */
	private Date createTime;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 更新时间 */
	private Date updateTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
