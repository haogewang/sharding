package com.szhq.iemp.reservation.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author wanghao
 * @date 2019/8/8
 */
@Entity(name = "role")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Trole implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    private String name;

    private String code;
    private Integer sort;
    private Integer type;
    private String remark;
    private String systemId;
    private String tenantId;
}
