package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TelectrombileVendor;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ElecmobileVenderService {
    /**
     * 查找所有电动车品牌
     */
    List<TelectrombileVendor> findAll();
    /**
     * 根据品品牌Id查找品牌
     */
    TelectrombileVendor findById(Integer id);
    /**
     * 根据索引查询
     */
    List<TelectrombileVendor> findByCretia(ElecmobileQuery query);
    /**
     * 添加
     */
    TelectrombileVendor addVendors(TelectrombileVendor entity);
    /**
     * 删除
     */
    Integer deleteElecVendorById(Integer id);
}
