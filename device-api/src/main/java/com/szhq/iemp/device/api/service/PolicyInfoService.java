package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TpolicyInfo;
import com.szhq.iemp.device.api.vo.PolicyInfo;
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

    Long save(TpolicyInfo entity);

    TpolicyInfo getPolicyById(long id);

    List<TpolicyInfo> getPolicyInfoByUserId(String userId);

    List<TpolicyInfo> saveAll(List<TpolicyInfo> entities);

    TpolicyInfo add(PolicyInfo entity);

    TpolicyInfo findByImei(String imei);

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
}
