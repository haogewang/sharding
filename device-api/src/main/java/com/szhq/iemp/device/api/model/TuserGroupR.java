package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户及用户组关系表
 */
@Entity(name = "user_group_r")
@Data
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TuserGroupR implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String userId;

    private String groupId;

    private String groupName;

    private String userName;

}