/**
 * @filename:SpuImage 2020年04月08日
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
public class SpuImage extends Model<SpuImage> {

	private static final long serialVersionUID = 1586348738090L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /**  */
	private Integer spuId;
    
	 /** 图片地址 */
	private String pictureUrl;
    
	 /** 权重(0主图，值越小排名越靠前) */
	private Integer index;
    
	 /** 图片类型（0：商品轮播图，1：商品详情图） */
	private Integer imageType;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
