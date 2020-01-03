package com.szhq.iemp.device.api.vo;

import com.szhq.iemp.device.api.model.Tinsurance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *导出保单信息
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportPolicyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idNumber;

    private String userName;

    private String plateNo;

    private String vin;

    private Date purchaseTime;

    private String vendor;

    private String elecType;

    private String policyName;

    private String imei;

    private String phone;

    private String sex;

    List<Tinsurance> insurances;

    private String operatorName;


}
