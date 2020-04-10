/**
 * @filename:Category 2020年04月08日
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
 * @Description:TODO(分类表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Category extends Model<Category> {

	private static final long serialVersionUID = 1586348736103L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /** 上级分类ID */
	private Integer parentId;
    
	 /** 排序等级 */
	private Integer categoryLevel;
    
	 /** 分类名称 */
	private String categoryName;
    
	 /** 分类全称 */
	private String fullName;
    
	 /** 分类描述 */
	private String description;
    
	 /** 排序权重 */
	private Integer sortRank;
    
	 /** 是否展示：0：否；1：是 */
	private Integer isShow;
    
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
