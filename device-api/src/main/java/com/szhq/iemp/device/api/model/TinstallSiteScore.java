package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author wanghao
 * @date 2019/9/2
 */
@Entity(name = "t_install_site_score")
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TinstallSiteScore extends BaseEntity {
    private static final long serialVersionUID = 7905905838420076720L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(40) COMMENT '安装人员Id'", nullable = false)
    private String workerId;

    @Column(columnDefinition = "varchar(40) COMMENT '安装人员姓名'", nullable = false)
    private String workerName;

    @NotNull(message = "安装点Id不能为空")
    @Column(columnDefinition = "INT COMMENT '安装点Id'", nullable = false)
    private Integer installSiteId;

    @Column(columnDefinition = "varchar(30) COMMENT '安装点名称'")
    private String installSiteName;

    @NotNull(message = "安装点分数不能为空")
    @Column(columnDefinition = "INT COMMENT '安装点分数'", nullable = false)
    private Integer installSiteScore;
    /**
     * 安装人员分数
     */
    @NotNull(message = "安装人员分数不能为空")
    @Column(columnDefinition = "INT COMMENT '安装人员分数'", nullable = false)
    private Integer installWorkerScore;
    /**
     * 评价
     */
    @Column(columnDefinition = "varchar(255) COMMENT '意见或建议'")
    private String comment;

    @NotEmpty(message = "设备号不能为空")
    @Column(columnDefinition = "varchar(50) COMMENT '设备号'", nullable = false, unique = true)
    private String imei;

    @Transient
    private String userName;

}
