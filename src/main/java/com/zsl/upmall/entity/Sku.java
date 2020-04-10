/**
 * @filename:Sku 2020年04月08日
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
 * @Description:TODO(sku表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Sku extends Model<Sku> {

	private static final long serialVersionUID = 1586348737321L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** 自增ID */
	private Integer id;
    
	 /** spu_id */
	private Integer spuId;
    
	 /** sku唯一编码 */
	private String uniqueCode;
    
	 /** sku名称(冗余spu_name) */
	private String skuName;
    
	 /** 成本价 */
	private BigDecimal costPrice;
    
	 /** 售价(价格为0时表示该商品仅支持使用积分兑换) */
	private BigDecimal retailPrice;
    
	 /** 销量 */
	private Integer salesVolume;
    
	 /** 库存 */
	private Integer stock;
    
	 /** 库存预警 */
	private Integer warnStock;
    
	 /** 状态(0：已下架；1：已上架) */
	private Integer status;
    
	 /** sku的图片 */
	private String skuPicture;
    
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
