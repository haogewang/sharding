package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TplatenoPrefix;
import com.szhq.iemp.reservation.api.vo.Region;

import java.util.List;

public interface PlateNoPrefixService {

//	List<ToperatorPlatenoPrefix> getPlateNoPrefixByOperatorId(Integer operatorId);

	List<TplatenoPrefix> findAllPlateNoPrefixByQuery(Region regionQuery);

	TplatenoPrefix save(TplatenoPrefix entity);

	Integer deleteById(Integer id);

    TplatenoPrefix findById(Integer id);
}
