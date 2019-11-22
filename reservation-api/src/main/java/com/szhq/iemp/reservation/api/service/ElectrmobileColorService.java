package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TelectrombileColor;

import java.util.List;

public interface ElectrmobileColorService {
    /**
     * 查找所有电动车颜色
     */
    List<TelectrombileColor> findAll();

    /**
     * 根据颜色id查找颜色
     */
    TelectrombileColor findById(Integer id);

    /**
     * 添加
     */
    TelectrombileColor addColors(TelectrombileColor entity);

    Integer deleteElecColorById(Integer id);
}
