package com.szhq.iemp.reservation.api.vo;

import com.szhq.iemp.reservation.api.model.TelectrombileUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelectrmobileVo extends TelectrombileUser {

    private static final long serialVersionUID = 1L;

    private String imei;
    private String plateNo;
    private String userName;
    private String phone;
    private String idNumber;
}
