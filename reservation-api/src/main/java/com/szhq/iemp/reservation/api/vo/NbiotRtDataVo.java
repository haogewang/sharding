package com.szhq.iemp.reservation.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wanghao
 * @date 2019/8/22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NbiotRtDataVo implements Serializable {

    private String imei;
    private Integer frequency;
    private String deviceName;
    private Long time;
    private Double lat;
    private Double lon;
    private String deviceVoltper;
    private String deviceVolt;
    private String type;
    private String address;
    private String deviceMode;
    private Date createTime;
    private Long unReadAlarmCount;
    private Integer speed;
    private Integer alt;

}
