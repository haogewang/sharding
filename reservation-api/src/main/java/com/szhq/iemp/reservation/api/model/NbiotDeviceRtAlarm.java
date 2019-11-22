package com.szhq.iemp.reservation.api.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name="nbiot_device_rt_alarm")
@Data
public class NbiotDeviceRtAlarm implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "varchar(32)", nullable = false)
	private String imei;

	private Date time;

	private double lat;

	private double lon;

	private int type;

	private int status;

	private Date createTime;

	private int operatorId;
	//报警时电池电压
	private double deviceVolt;
	// 紧急报警事件
	private int emergencyAlert;
	//宿主状态
	private int hostMode;
	//运动状态
	private int motionStat;
	//设备故障码
	private int devCode;
	//设备模式
	private int deviceMode;
	/**
	 * 设备类型
	 */
	private String devType;

}