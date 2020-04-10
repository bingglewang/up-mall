/**
 * @filename:UserFeedback 2020年04月08日
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
 * @Description:TODO(用户反馈实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserFeedback extends Model<UserFeedback> {

	private static final long serialVersionUID = 1586348738963L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** 主键id */
	private Integer id;
    
	 /** 用户id */
	private Integer userId;
    
	 /** 查看状态（0未阅1已阅） */
	private Integer readStatus;
    
	 /** 反馈内容 */
	private String content;
    
	 /** 手机号码 */
	private Long phone;
    
	 /** 反馈图片（以"；"分割） */
	private String picture;
    
	 /** 系统回复 */
	private String systemReply;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 创建时间 */
	private Date createTime;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 修改时间 */
	private Date modifyTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
