package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.BaseQuery;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TiotType;

public interface IotTypeService {
    /**
     * 无线服务商列表
     */
    MyPage<TiotType> findAllByCriteria(Integer page, Integer size, String sorts, String orders, BaseQuery myQuery);

    /**
     * 添加
     */
    TiotType save(TiotType entity);

    /**
     * 删除
     */
    Integer deleteById(Integer id);

    /**
     * 通过名字查找
     */
    TiotType findByName(String name);

    /**
     * 通过id查找
     */
    TiotType findById(Integer id);
}
