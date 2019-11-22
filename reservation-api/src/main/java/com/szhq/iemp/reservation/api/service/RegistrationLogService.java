package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TregistrationLog;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import org.springframework.data.domain.Page;

public interface RegistrationLogService {
    /**
     * 保存备案日志
     */
    TregistrationLog save(TregistrationLog entity);
    /**
     * 模糊条件查询
     */
    Page<TregistrationLog> findRegistrationCriteria(Integer offset, Integer limit, String sort, String order, RegisterQuery query);
    /**
     *修改
     */
    Integer updateByRegisterId(Long registerId);
}
