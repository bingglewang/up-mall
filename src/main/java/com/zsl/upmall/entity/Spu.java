/**
 * @filename:Spu 2020年04月08日
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
 * @Description:TODO(spu表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Spu extends Model<Spu> {

	private static final long serialVersionUID = 1586348737889L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /** 商铺id，为0表示自营 */
	private Integer shopId;
    
	 /** 商品唯一编码 */
	private String uniqueCode;
    
	 /** 商品名称 */
	private String spuName;
    
	 /** 副标题 */
	private String subName;
    
	 /** 分类id */
	private Integer categoryId;
    
	 /** 状态(0：已下架；1：已上架) */
	private Integer status;
    
	 /** 展示销量，大于-1则为虚拟销量，否则计算sku总销量 */
	private Integer showSales;
    
	 /** 备用运费（单品运费模式下未配置单品运费时此值生效） */
	private BigDecimal freight;
    
	 /** 单品运费模板id */
	private Integer freightTemplateId;
    
	 /** 品牌id */
	private Integer brandId;
    
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
