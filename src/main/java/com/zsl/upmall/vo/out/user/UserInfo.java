package com.zsl.upmall.vo.out.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo {
    /** 用户昵称或网络名称 */
    private String nickName;

    /** 真实姓名 */
    private String realName;

    /** 用户手机号码 */
    private String mobile;

    /** 身份证号码 */
    private String identityCard;

    /** 用户头像图片 */
    private String avatar;

    /** 性别：0 未知， 1男， 1 女 */
    private Integer gender;

    /** 用户余额 */
    private Double balance;

    /** 总积分 */
    private Long integral;

    /** 会员推荐人id */
    private Integer memberRecommendedId;

    /** 代理商推荐人id */
    private Integer agentRecommendedId;

    /** 0 新人，1 铜牌，2 银牌，3金牌，4，事业伙伴，5经销商，6官方代理 */
    private Integer userLevel;

    /** 用户分享码 */
    private String shareId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    /** 创建时间 */
    private Date addTime;
}
