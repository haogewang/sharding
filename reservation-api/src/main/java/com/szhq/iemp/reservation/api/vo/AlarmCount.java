package com.szhq.iemp.reservation.api.vo;

import com.szhq.iemp.reservation.api.model.EsNbiotDeviceAlarm;
import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Tuser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmCount implements Serializable{


	private static final long serialVersionUID = 1L;

	private EsNbiotDeviceAlarm esAlarm;

	private Telectrmobile elec;

	private Tuser user;
	
	
}
