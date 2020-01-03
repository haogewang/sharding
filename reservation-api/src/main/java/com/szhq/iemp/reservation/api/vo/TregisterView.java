package com.szhq.iemp.reservation.api.vo;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
public class TregisterView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer registrationId;

    private Date createTime;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 车主姓名
     */
    private String username;
    /**
     * 登录账号
     */
    private String phone;
    /**
     * 身份证号
     */
    private String idNumber;
    /**
     * 安装点名称
     */
    private String installSiteName;
    /**
     * 设备号
     */
    private String imei;
    /**
     * 保单号
     */
    private String policyNo;
    /**
     * 户籍地址
     */
    private String birthPlace;
    /**
     * 联系方式
     */
    private String contactPhone;
    /**
     * 车辆种类
     */
    private String elecType;
    /**
     * 车辆品牌
     */
    private String elecVendor;
    /**
     * 车辆颜色
     */
    private String elecColor;
    /**
     * 车架号
     */
    private String vin;
    /**
     * 购车日期
     */
    private Date purchaseTime;
    /**
     * 安装点负责人
     */
    private String personInCharge;
    /**
     * 安装点电话
     */
    private String installSitePhone;
    /**
     * 派出所
     */
    private String policeStation;
    /**
     * 上线状态
     */
    private String onlineStatus;
    /**
     * 运营商
     */
    private String isp;

    private Integer iotTypeId;

    private Integer installSiteId;

    private Integer operatorId;

    private Integer storehouseId;

    private Integer residentId;

    private Integer manufacturerId;

}