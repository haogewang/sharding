package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TpolicePrecinct;

import java.util.List;

public interface PolicePrecinctService {
    /**
     * 列表
     */
    List<TpolicePrecinct> findAll();

    /**
     * 根据id查找
     */
    TpolicePrecinct findById(String id);

    /**
     * 添加
     */
    TpolicePrecinct add(TpolicePrecinct entity);

    /**
     * 删除
     */
    Integer deleteById(String id);

}
