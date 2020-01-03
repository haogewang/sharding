package com.szhq.iemp.device.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szhq.iemp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name="t_adress_region")
@Data
@EntityListeners(AuditingEntityListener.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaddressRegion extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String areaName;

	private String areaCode;

	private String areaShort;

	private String areaIsHot;

	private Integer areaSequence;

	@OneToOne(fetch = FetchType.LAZY, cascade= {CascadeType.REFRESH})
	@JoinColumn(columnDefinition="INT COMMENT '从属区域Id' ", name = "parent_id", referencedColumnName = "id" ,foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@NotFound(action= NotFoundAction.IGNORE)
	private TaddressRegion region;

//	@Transient
//	private TaddressRegion parent;


}
