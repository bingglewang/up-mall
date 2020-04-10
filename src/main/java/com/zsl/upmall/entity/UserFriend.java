/**
 * @filename:UserFriend 2020年04月08日
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
 * @Description:TODO(用户好友表实体类)
 * 
 * @version: V1.0
 * @author: binggleWang
 * 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserFriend extends Model<UserFriend> {

	private static final long serialVersionUID = 1586348739068L;
	
	@TableId(value = "id", type = IdType.AUTO)
	 /** 主键id */
	private Integer id;
    
	 /** 用户id */
	private Integer userId;
    
	 /** 好友id */
	private Integer friendId;
    
	 /** 推荐码链（顶级推荐码/下级推荐码/下下级推荐码） 用/分隔 */
	private String tree;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	 /** 创建时间 */
	private Date createTime;
    

	@Override
    protected Serializable pkVal() {
        return this.id;
    }
}
