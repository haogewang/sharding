package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.szhq.iemp.common.model.BaseEntity;
import com.szhq.iemp.device.api.vo.RegisterVo;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author wanghao
 * @date 2019/10/17
 */
@Data
@Entity(name = "t_device_inventory")
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TdeviceInventory extends BaseEntity {

    private static final long serialVersionUID = -6022878272751664164L;

//    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "设备类型不能为空")
    @Column(columnDefinition = "varchar(20) COMMENT '设备类型'", nullable = false)
    private String devtype;

    @NotEmpty(message = "设备名称不能为空")
    @Column(columnDefinition = "varchar(20) COMMENT '设备名称'", nullable = false)
    private String devname;

    @Column(columnDefinition = "INT COMMENT '设备状态(0:未安装,1:已安装,2:更换)'")
    private Integer devstate = 0;

    private String devDesc;

    @Column(columnDefinition = "varchar(30)")
    private String snNo;

    @NotEmpty(message = "设备号不能为空")
    @Column(columnDefinition = "varchar(40) COMMENT '设备号'", unique = true, nullable = false)
    private String imei;

    @Column(columnDefinition = "varchar(20)")
    private String imsi;

    @Column(columnDefinition = "varchar(30)")
    private String iccid;

    @NotEmpty(message = "设备产品型号不能为空")
    @Column(columnDefinition = "varchar(20) COMMENT '产品型号'", nullable = false)
    private String modelNo;

    @Column(columnDefinition = "varchar(20) COMMENT '箱号'")
    private String boxNumber;

    @NotEmpty(message = "软件版本不能为空")
    @Column(columnDefinition = "varchar(50) COMMENT '软件版本'", nullable = false)
    private String swVersion;

    @NotEmpty(message = "iot设备号不能为空")
    @Column(columnDefinition = "varchar(100)", nullable = false)
    private String iotDeviceId;

    @Column(columnDefinition = "varchar(20) COMMENT '运营商(CMCC/CT/CU)'", nullable = false)
    @NotEmpty(message = "运营商不能为空")
    private String isp;

    @Column(columnDefinition = "datetime COMMENT '分配到安装点时间'")
    private Date dispatchTime;

    @Column(columnDefinition = "datetime COMMENT '入库时间'")
    private Date putStorageTime;
    /**
     * 入库人员Id
     */
    private String putStorageUserId;

    @NotNull(message = "仓库Id不能为空")
    @Column(columnDefinition = "INT COMMENT '仓库Id'", nullable = false)
    private Integer storehouseId;

    private String storehouseName;

    @NotNull(message = "归属地Id不能为空")
    @Column(columnDefinition = "INT COMMENT '归属地Id'", nullable = false)
    private Integer regionId;

    @Column(columnDefinition = "varchar(20) COMMENT '归属地名称'")
    private String regionName;

    @Column(columnDefinition = "INT COMMENT '安装点id'")
    private Integer installSiteId;

    @Column(columnDefinition = "varchar(50) COMMENT '安装点名称'")
    private String installSiteName;

    @NotNull(message = "运营公司Id不能为空")
    @Column(columnDefinition = "INT COMMENT '运营公司Id'", nullable = false)
    private Integer operatorId;

    @Column(columnDefinition = "varchar(30) COMMENT '运营公司名称'")
    private String operatorName;

    @NotNull(message = "无线服务商Id不能为空")
    @Column(columnDefinition = "INT COMMENT '无线服务商名称'", nullable = false)
    private Integer iotTypeId;

    @Column(columnDefinition = "varchar(30) COMMENT '无线服务商名称'")
    private String iotTypeName;

    @NotNull(message = "设备厂商Id不能为空")
    @Column(columnDefinition = "INT COMMENT '设备厂商Id'", nullable = false)
    private Integer manufactorId;

    @Column(columnDefinition = "varchar(20) COMMENT '设备厂商名称'")
    private String manufactorName;

    @Column(columnDefinition = "INT COMMENT '组Id'")
    private Integer groupId;

    private Boolean isActive = true;
    /**
     * 发货批号
     */
    private String deliverSn;

    @Transient
    private RegisterVo registerVo;

    @Transient
    private List<String> boxNumbers;

}
