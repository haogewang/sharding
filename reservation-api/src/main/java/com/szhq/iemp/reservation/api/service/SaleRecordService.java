package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TsaleRecord;

/**
 * @author wanghao
 * @date 2019/11/19
 */
public interface SaleRecordService {

    TsaleRecord add(TsaleRecord entity);

    TsaleRecord findByImeiAndMode(String imei, Integer mode);
}
