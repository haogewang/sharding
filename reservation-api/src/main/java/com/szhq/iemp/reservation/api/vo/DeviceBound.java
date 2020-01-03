package com.szhq.iemp.reservation.api.vo;

import com.szhq.iemp.reservation.api.model.EsNbiotDeviceAlarm;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Tuser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceBound implements Serializable{


	private static final long serialVersionUID = 1L;

	@NotEmpty(message = "设备号不能为空")
	private String imei;

	@NotEmpty(message = "用户Id不能为空")
	private String userId;

//	@NotEmpty(message = "设备名不能为空")
	private String deviceName;

	private String plateNumber;

	private Integer frequency = 86400;

	private String type = "W302";

//	private Long registerId;


}
