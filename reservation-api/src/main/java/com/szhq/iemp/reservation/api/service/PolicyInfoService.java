package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TpolicyInfo;

import java.util.List;

/**
 * @author wanghao
 * @date 2019/10/24
 */
public interface PolicyInfoService {

    Long save(TpolicyInfo entity);

    List<TpolicyInfo> saveAll(List<TpolicyInfo> entities);

    TpolicyInfo findByImeiAndUserId(String imei, String userId);

    TpolicyInfo findByImeiAndUserIdisNull(String imei);
}
