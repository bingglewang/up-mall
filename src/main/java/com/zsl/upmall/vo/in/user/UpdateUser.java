package com.zsl.upmall.vo.in.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UpdateUser {

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
}
