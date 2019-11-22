package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author wanghao
 * @date 2019/8/8
 */
@Entity(name = "user_push")
@Data
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TuserPush implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "varchar(50)")
    private String id;

    /**
     * 类型(1:极光)
     */
    private int type;

    /**
     * "系统id"
     */
    private int systemId;

    /**
     * "pushId"
     */
    private String channelId;

    /**
     * "用户编号"
     */
    private String userId;

    /**
     * "设备id"
     */
    private String deviceId;

    /**
     * "创建时间"
     */
    private String createTime;

    /**
     * "修改时间"
     */
    private String updateTime;

    /**
     * 电话推送的状态 默认为0 关闭  1 开启
     */
    private int status;
}
