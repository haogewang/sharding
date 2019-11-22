package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TactiveInfo;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.ActiveQuery;
import com.szhq.iemp.device.api.vo.query.SaleRecordQuery;

import java.util.Date;
import java.util.List;

/**
 * @author wanghao
 * @date 2019/10/24
 */
public interface ActiveInfoService {

    Long save(TactiveInfo entity);

    Integer activeImei(String imei, String userId);
    /**
     * 统计今日激活人员激活数量
     */
    Integer countTodayActiveByActivatorId(String activatorId, Integer mode);

    MyPage<TactiveInfo> findAllByCriteria(Integer page, Integer size, String sorts, String orders, ActiveQuery myQuery);
    /**
     * 退货
     */
    Integer back(String imei, String userId);
    /**
     * 根据用户Id查看设备激活信息
     */
    List<TactiveInfo> getActiveByUserId(String userId);

    List<TactiveInfo> findByImeis(List<String> imeis);
    /**
     * 根据运营公司ids统计激活数量
     */
    Integer countActiveByOperatorIds(List<Integer> operatorIds, int mode, Date startTime, Date endTime);
    /**
     *根据组ids统计激活数量
     */
    Integer countActiveByGroupIds(List<String> groupIds, int mode, Date startTime, Date endTime);

    List<ActiveDeviceCount> activeStatisticByOperatorId(SaleRecordQuery query);

    List<ActiveDeviceCount> activeStatisticByGroupId(SaleRecordQuery query);
}
