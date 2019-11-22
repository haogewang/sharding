package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.Treservation;
import com.szhq.iemp.reservation.api.vo.query.ReservationQuery;

import java.util.List;

public interface ReservationService {
    /**
     * 查找预约列表
     */
    MyPage<Treservation> findReservationNoCriteria(Integer page, Integer size, String sort, String order);

    /**
     * 根据条件查找预约列表
     */
    MyPage<Treservation> findReservationCriteria(Integer page, Integer size, String sort, String order, ReservationQuery reservationQuery);

    /**
     * 修改预约
     */
    Integer update(Treservation reservation);

    /**
     * 保存预约
     */
    Integer save(Treservation reservation);

    /**
     * 删除
     */
    Treservation delete(Integer id);

    /**
     * 根据时间段统计
     */
    Integer countByExample(Integer installSiteId, long start, long end);

    /**
     * 根据预约号获取预约信息
     */
    Treservation getInfo(String number);

    /**
     * 删除三天前数据
     */
    Integer deleteThreeDaysAgoData();

    /**
     * 根据车架号查找预约信息
     */
    Treservation findByVin(String vin);

    /**
     * 根据电机号查找预约信息
     */
    List<Treservation> findByMotorNumber(String motorNumber);
    /**
     *根据账号查找
     */
    Treservation findByPhone(String phone);

    /**
     * 清除redis缓存
     */
    void deleteRedisKey();

    /**
     * 根据预约号删除
     */
    int deleteByReserNo(String reservationNo);
}
