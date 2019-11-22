package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TelectrombileType;

import java.util.List;

public interface ElecmobileTypeService {

    /**
     * 查找所有电动车类型
     */
    List<TelectrombileType> findAll();

    /**
     * 根据电类型id查找类型
     */
    TelectrombileType findById(Integer id);
    /**
     * 添加
     */
    TelectrombileType addTypes(TelectrombileType entity);

    Integer deleteElecTypeById(Integer id);
}
