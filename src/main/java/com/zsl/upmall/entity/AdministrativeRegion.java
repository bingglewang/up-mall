/**
 * @filename:AdministrativeRegion 2020年04月08日
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
public class AdministrativeRegion extends Model<AdministrativeRegion> {

	private static final long serialVersionUID = 1586348735407L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** id主键 */
	private Integer id;
    
	 /** 国家代码ID */
	private String countryCode;
    
	 /** 省份代码ID */
	private String provinceCode;
    
	 /** 城市代码ID */
	private String cityCode;
    
	 /** 区域代码ID */
	private String areaCode;
    
	 /** 地址全称 */
	private String siteFullName;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 更新时间 */
	private Date updateTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
