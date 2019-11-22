package com.szhq.iemp.reservation.api.vo.query;

import com.szhq.iemp.common.vo.BaseQuery;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ReservationQuery extends BaseQuery {

    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    private String ownerName;
    /**
     * 预约号
     */
    private String reservationNumber;
    /**
     * 身份证号
     */
    private String idNumber;
    /**
     * 预约时间
     */
    private Long reservationTime;
    /**
     * 是否有安装点
     */
    private boolean isHaveInstallSite = false;
    /**
     * 登录账号
     */
    private String phone;

}
