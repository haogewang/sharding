package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.TnoTrackerElec;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.vo.DeviceBound;
import com.szhq.iemp.reservation.api.vo.NotrackerRegister;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NoTrackerElecmobileService {
    /**
     * 查找所有电动车
     */
    MyPage<TnoTrackerElec> findAllByCriteria(Integer page, Integer size, String sort, String order, ElecmobileQuery query);

    MyPage<TnoTrackerElec> findAllByCretia(Integer page, Integer size, String sorts, String orders, ElecmobileQuery query);

    /**
     * 根据车牌查找
     */
    TnoTrackerElec findByPlateNumber(String plateNo);

    /**
     * 根据车架号查找
     */
    TnoTrackerElec findByVin(String vin);
    /**
     * 根据id查找
     */
    TnoTrackerElec findById(Long id);

    /**
     * 根据userId查找
     */
    List<TnoTrackerElec> findByUserId(String id);

    /**
     * 添加电动车(有imei时备案)(302模式)
     */
    Integer add(NotrackerRegister notrackerRegister, HttpServletRequest request);

    /**
     * 根据id删除
     */
    Integer deleteById(Long id);

    /**
     * 根据userId删除
     */
    Integer deleteByUserId(String userId);

    /**
     * 绑定设备(310模式)
     */
    Long boundDevice(DeviceBound deviceBound);
    /**
     * 解绑设备(310模式，会删除电动车表)
     */
    Integer unBoundDevice(String imei);
    /**
     * 解绑设备（不删除电动车）
     */
    Integer unboundDeviceNoDeleteElec(String imei);
    /**
     * 解绑电动车或电动车及设备
     */
    Integer unBoundByElecIdAndUserId(String userId, Long elecId);
    /**
     * 修改车辆信息
     */
    Integer update(TnoTrackerElec tnoTrackerElec);

    /**
     * 添加用户及电动车
     */
    String addUserAndElecmobile(NotrackerRegister data, HttpServletRequest request);

    Integer deleteNoTrackerRedisKey();

    TnoTrackerElec findByPlateNumberAndOperatorIdsIn(String plateNo, List<Integer> operatorIdList);
}
