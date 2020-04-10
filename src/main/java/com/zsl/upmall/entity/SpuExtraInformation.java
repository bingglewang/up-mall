/**
 * @filename:SpuExtraInformation 2020年04月08日
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
public class SpuExtraInformation extends Model<SpuExtraInformation> {

	private static final long serialVersionUID = 1586348737994L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /**  */
	private Integer id;
    
	 /**  */
	private Integer spuId;
    
	 /** 服务说明 */
	private String serviceNote;
    
	 /** 推荐理由 */
	private String recommendReason;
    
	 /** 故事主题 */
	private String storyTheme;
    
	 /** 商品开发者 */
	private String goodsDeveloper;
    
	 /** 故事作者 */
	private String storyAuthor;
    
	 /** 作者个性签名 */
	private String authorSignature;
    
	 /** 故事内容 */
	private String storyContent;
    
	 /** 富文本内容 */
	private String introduction;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
