package com.szhq.iemp.reservation.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class RegisterQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;
    /**
     * 身份证号
     */
    private String idNumber;
    /**
     * 真实姓名
     */
    private String realname;
    /**
     * 登录账号
     */
    private String phone;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 设备号
     */
    private String imei;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * app使用字段
     */
    private String custom;
    /**
     * 备案id
     */
    private Long registerId;
    /**
     * 备案记录类型
     */
    private Integer type;
    /**
     * 产品类型(302/310)
     */
    private String modelNo;

    private Boolean isHaveImei;
    /**
     * 是否加密
     */
    private Boolean encrypt = false;

}
