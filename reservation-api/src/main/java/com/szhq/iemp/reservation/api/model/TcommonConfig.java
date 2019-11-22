package com.szhq.iemp.reservation.api.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name="t_common_config")
@DynamicUpdate
@DynamicInsert
@Data
public class TcommonConfig implements Serializable {

	private static final long serialVersionUID = 5796343903113315403L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String name;

	private String value;

	
	
}
