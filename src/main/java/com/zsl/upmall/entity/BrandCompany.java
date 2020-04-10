/**
 * @filename:BrandCompany 2020年04月08日
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
 * @Description:TODO(公司表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BrandCompany extends Model<BrandCompany> {

	private static final long serialVersionUID = 1586348735967L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Long id;
    
	 /** 公司名称 */
	private String companyName;
    
	 /** 公司logo */
	private String companyLogo;
    
	 /** 公司主页 */
	private String companyHome;
    
	 /** 公司描述 */
	private String description;
    
	 /** 排序权重 */
	private Integer sortRank;
    
	 /** 合作状态：0：终止合作；1：正常合作；2：暂停合作 */
	private Integer status;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /**  */
	private Date createTime;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /**  */
	private Date updateTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
