package com.szhq.iemp.reservation.api.vo;

import com.szhq.iemp.reservation.api.model.Telectrmobile;
import com.szhq.iemp.reservation.api.model.Tuser;
import lombok.Data;

import javax.validation.Valid;

@Data
public class NotrackerRegister {

	private Tuser user;

	private Telectrmobile elec;
	/**
	 * 预约号
	 */
	private String reservationNo;

}
