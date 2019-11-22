package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.Tinsurance;
import com.szhq.iemp.device.api.vo.PolicyName;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface InsuranceService {

    /**
     * 查询
     */
    List<Tinsurance> list();
    /**
     * 分页查询
     */
    Page<Tinsurance> page(int page,int size);

    /**
     * 添加
     */
    Tinsurance add(Tinsurance entity);
    /**
     * 删除
     */
    Integer delete(Integer id);
    /**
     * 根据id查询
     */
    Tinsurance findById(Integer id);

    /**
     * 根据ids查询
     */
    List<Tinsurance> findByIdIn(List<Integer> ids);
    /**
     * 查询所有保险名称
     */
    List<PolicyName> getAllNames();
    /**
     * 查询所有保险类型
     */
    Map<Integer, String> getAllTypes();

    String getNameByCode(Integer code);

    String getTypeByCode(Integer code);
}
