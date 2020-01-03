package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TpolicyInfo;
import com.szhq.iemp.device.api.vo.DeviceVo;
import com.szhq.iemp.device.api.vo.PolicyInfo;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.PolicyQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wanghao
 * @date 2019/10/24
 */
public interface PolicyInfoService {
    /**
     *列表
     */
    MyPage<TpolicyInfo> findAllByCriteria(Integer page, Integer size, String sorts, String orders, PolicyQuery myQuery);

    MyPage<DeviceVo> devices(Integer offset, Integer limit, DeviceQuery query);

    Long save(TpolicyInfo entity);

    TpolicyInfo getPolicyById(long id);

    List<TpolicyInfo> getPolicyInfoByUserId(String userId);

    List<TpolicyInfo> saveAll(List<TpolicyInfo> entities);

    void batchSave(List<TpolicyInfo> entities);

    void batchUpdate(List<TpolicyInfo> entities);

    TpolicyInfo add(PolicyInfo entity);

    TpolicyInfo findByImei(String imei);

    List<TpolicyInfo> findByImeis(List<String> imeis);

    TpolicyInfo findByImeiAndUserId(String imei, String userId);

    TpolicyInfo findByPlateNo(String plateNo);
    /**
     * 导出保单
     */
    void exportExcel(HttpServletResponse response, PolicyQuery query);

    Integer deleteNoInstalledPolicyByImeis(List<String> imeis);
    /**
     * 修改保单
     */
    Integer updatePolicy(TpolicyInfo tpolicyInfo);

    void deleteByIds(List<Long> ids);

    //临时方法 补充未添加保单的设备
    Integer getLostPolicys();
    /**
     * 初始化保单信息
     */
    Long initializePolicy(String imei);

    Long addNewPolicy(TpolicyInfo policyInfo);
}
