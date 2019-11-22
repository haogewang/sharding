package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.Tregistration;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;

import javax.servlet.http.HttpServletResponse;

public interface RegistrationService {


    /**
     * 根据条件查询
     */
    MyPage<Tregistration> findRegistrationCriteria(Integer page, Integer size, String sort, String order, RegisterQuery query);

    /**
     * 根据条件统计数量
     */
    Long countByQuery(RegisterQuery query);

    /**
     * 根据imei查找备案信息
     */
    Tregistration findByImei(String imei);
    /**
     * 备案注册
     */
    Tregistration register(Tregistration data, Boolean isCreateUser);

    /**
     * 修改备案
     */
    Integer updateRegistration(Tregistration registration);

    /**
     * 保存备案
     */
    Tregistration save(Tregistration registration);

    /**
     * 只删除备案表信息
     */
    Tregistration delete(Long id);

    /**
     * 级联删除备案
     */
    void deleteRegistration(Long id, String imei, Boolean isDeleteUser);

    /**
     * 更换设备
     */
    Integer changeImei(Long registerId, String newImei, RegisterQuery query);

    /**
     * 添加备案(电动车或用户一方存在时调用)
     */
    Tregistration addRegistration(Tregistration data);

    /**
     * 导出备案数据
     */
     void exportExcel(HttpServletResponse response, RegisterQuery query);

    /**
     * 根据iotDeviceId查找是否备案
     */
    Tregistration findByIotDeviceId(String deviceId);

    /**
     * 根据id查找备案信息
     */
    Tregistration findById(Long id);

    /**
     * 根据userId查找该用户备案信息数量
     */
    Integer countByUserId(String userId);

    /**
     * 删除redis缓存
     */
    void deleteRegisterRedis();

    /**
     * 根据imei删除备案
     */
    Integer deleteByImei(String imei);

    /**
     * 查看备案详情
     */
    Tregistration getInfoById(Long id);

    /**
     * 通过登录账号查备案信息
     */
    Tregistration getInfoByPhone(String phone);
}
