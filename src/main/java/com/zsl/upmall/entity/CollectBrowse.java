/**
 * @filename:CollectBrowse 2020年04月08日
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
public class CollectBrowse extends Model<CollectBrowse> {

	private static final long serialVersionUID = 1586348736339L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /**  */
	private Integer userId;
    
	 /** 目标种类（0：收藏商家，1：收藏商品，2：浏览商品） */
	private Integer targetType;
    
	 /** 目标id */
	private Integer targetId;
    
	 /** 操作状态（0：取消或隐藏，1：收藏或显示） */
	private Integer collectStatus;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 操作时间 */
	private Date operationTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
