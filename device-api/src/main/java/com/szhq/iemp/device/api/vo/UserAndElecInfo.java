package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAndElecInfo implements Serializable {


    private static final long serialVersionUID = 1L;

    private String username;

    private String plateNumber;

    private String imei;

}
