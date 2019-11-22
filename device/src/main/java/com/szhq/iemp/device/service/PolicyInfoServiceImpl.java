package com.szhq.iemp.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.ElectrombileExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.ExportExcelUtils;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.ExportExcelData;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.model.Tinsurance;
import com.szhq.iemp.device.api.model.TpolicyInfo;
import com.szhq.iemp.device.api.model.TuserInsurance;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.ElectrmobileService;
import com.szhq.iemp.device.api.service.InsuranceService;
import com.szhq.iemp.device.api.service.PolicyInfoService;
import com.szhq.iemp.device.api.vo.ExportPolicyInfo;
import com.szhq.iemp.device.api.vo.PolicyInfo;
import com.szhq.iemp.device.api.vo.query.PolicyQuery;
import com.szhq.iemp.device.repository.PolicyInfoRepository;
import com.szhq.iemp.device.repository.UserInsuranceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PolicyInfoServiceImpl implements PolicyInfoService {

    @Resource
    private PolicyInfoRepository policyInfoRepository;
    @Resource
    private UserInsuranceRepository userInsuranceRepository;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private InsuranceService insuranceService;

    @Override
    public TpolicyInfo add(PolicyInfo entity) {
        if(entity.getElectrombile() == null || StringUtils.isEmpty(entity.getElectrombile().getImei()) ||
                StringUtils.isEmpty(entity.getUser().getId()) || StringUtils.isEmpty(entity.getElectrombile().getPlateNumber())){
            log.error("wrong parameter." + JSONObject.toJSONString(entity));
            throw new NbiotException(400, "");
        }
        String userId = entity.getUser().getId();
        String imei = entity.getElectrombile().getImei();
        String plateNo = entity.getElectrombile().getPlateNumber();
        TpolicyInfo policyInfo = findByPlateNo(plateNo);
        if(policyInfo != null){
            //ElectrombileExceptionEnum.E_00010
            throw new NbiotException(3000007, "");
        }
        policyInfo = findByImeiAndUserId(imei, userId);
        if(policyInfo == null){
            //ElectrombileExceptionEnum.E_00011
            throw new NbiotException(3000008, "");
        }
        BeanUtils.copyProperties(entity,policyInfo, PropertyUtil.getNullProperties(entity));
        policyInfo.setActiveTime(new Date());
        policyInfo.setStartTime(TimeStampUtil.getNextMonthStartTime());
        policyInfo.setEndTime(TimeStampUtil.getYearTimeByPeriod(TimeStampUtil.getNextMonthStartTime(), entity.getPeriod()));
        policyInfo.setPlateNo(plateNo);
        policyInfo.setUserId(userId);
        policyInfo.setIsEffective(true);
        TdeviceInventory device = deviceInventoryService.findByImei(imei);
        if(device != null){
            policyInfo.setOperatorId(device.getOperatorId());
        }
        JSONObject jsonObject = new JSONObject();
        if(entity.getUser() != null){
            String user = JSONObject.toJSONString(entity.getUser());
            jsonObject.put("user", user);
        }
        if(entity.getElectrombile() != null){
            String elec = JSONObject.toJSONString(entity.getElectrombile());
            jsonObject.put("elec", elec);
        }
        policyInfo.setInfo(jsonObject.toJSONString());
        TpolicyInfo result = policyInfoRepository.save(policyInfo);
        if(entity.getInsuranceIds() != null && entity.getInsuranceIds().size() > 0){
            entity.getInsuranceIds().forEach(insuranceId -> {
                TuserInsurance userInsurance = new TuserInsurance();
                //遍历循环存入数据库
                userInsurance.setPolicyId(result.getId());
                userInsurance.setInsuranceId(insuranceId);
                userInsurance.setPeriod(entity.getPeriod());
                userInsuranceRepository.save(userInsurance);
            });
        }

        return result;
    }

    @Override
    public TpolicyInfo findByPlateNo(String plateNo) {
        return policyInfoRepository.findByPlateNo(plateNo);
    }

    @Override
    public TpolicyInfo findByImei(String imei) {
        return policyInfoRepository.findByImei(imei);
    }

    @Override
    public TpolicyInfo findByImeiAndUserId(String imei, String userId) {
        return policyInfoRepository.findByImeiAndUserId(imei, userId);
    }

    @Override
    public MyPage<TpolicyInfo> findAllByCriteria(Integer page, Integer size, String sorts, String orders, PolicyQuery myQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        if (sort == null) {
            throw new NbiotException(400, "");
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TpolicyInfo> pages = policyInfoRepository.findAll(new Specification<TpolicyInfo>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TpolicyInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (myQuery != null) {
                    if (myQuery.getOperatorId() != null) {
                        list.add(criteriaBuilder.equal(root.get("operatorId").as(Integer.class), myQuery.getOperatorId()));
                    }
                    if (StringUtils.isNotEmpty(myQuery.getImei())) {
                        list.add(criteriaBuilder.equal(root.get("imei").as(String.class), myQuery.getImei()));
                    }
                    if (myQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(myQuery.getOperatorIdList()));
                    }
                }
                list.add(criteriaBuilder.isTrue(root.get("isEffective").as(Boolean.class)));
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TpolicyInfo>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public Long save(TpolicyInfo entity) {
        TpolicyInfo policyInfo = policyInfoRepository.save(entity);
        return policyInfo.getId();
    }

    @Override
    public TpolicyInfo getPolicyById(long id) {
        return policyInfoRepository.getPolicyById(id);
    }

    @Override
    public List<TpolicyInfo> getPolicyInfoByUserId(String userId) {
        return policyInfoRepository.getPolicyInfoByUserId(userId);
    }

    @Override
    public List<TpolicyInfo> saveAll(List<TpolicyInfo> entities) {
        return policyInfoRepository.saveAll(entities);
    }


    @Override
    public void exportExcel(HttpServletResponse response, PolicyQuery query) {
        List<ExportPolicyInfo> list = findAllByDate(query);
        ExportExcelData data = addExcelData(list);
        try {
            String excelName = TimeStampUtil.getTs(query.getStartTime()) + "至" + TimeStampUtil.getTs(query.getEndTime()) + "保单统计.xls";
            ExportExcelUtils.exportExcel(response, excelName, data);
        } catch (Exception e) {
            log.error("e", e);
            throw new NbiotException(500, "");
        }
    }

    @Override
    public Integer deleteNoInstalledPolicyByImeis(List<String> imeis) {
        Integer count = policyInfoRepository.deleteNoInstalledPolicyByImeis(imeis);
        return count;
    }

    @Override
    public Integer updatePolicy(TpolicyInfo tpolicyInfo) {
        TpolicyInfo policyInfo = findByImei(tpolicyInfo.getImei());
        if(policyInfo != null){
            policyInfo.setUserId(tpolicyInfo.getUserId());
            policyInfo.setIsEffective(tpolicyInfo.getIsEffective());
            policyInfo.setActiveTime(tpolicyInfo.getActiveTime());
            policyInfo.setEndTime(tpolicyInfo.getEndTime());
            policyInfo.setInfo(tpolicyInfo.getInfo());
            policyInfo.setStartTime(tpolicyInfo.getStartTime());
            save(policyInfo);
            return 1;
        }
        return 0;
    }

    private List<ExportPolicyInfo> findAllByDate(PolicyQuery query) {
        List<ExportPolicyInfo> result = new ArrayList<>();
        List<TpolicyInfo> list = new ArrayList<>();
        if(query.getOperatorIdList() != null){
            list = policyInfoRepository.findAllByDate(query.getStartTime(), query.getEndTime(), query.getOperatorIdList());
        }else{
            list = policyInfoRepository.findAllByDate(query.getStartTime(), query.getEndTime());
        }
        if(list.isEmpty()){
            return result;
        }
        for(TpolicyInfo policyInfo : list){
            ExportPolicyInfo exportPolicyInfo = new ExportPolicyInfo();
            String info = policyInfo.getInfo();
            String policyName = policyInfo.getName();
            String imei = policyInfo.getImei();
            JSONObject infoJson = JSON.parseObject(info);
            JSONObject userJson = infoJson.getJSONObject("user");
            JSONObject elecJson = infoJson.getJSONObject("elec");
            exportPolicyInfo.setIdNumber(userJson.getString("idNumber"));
            exportPolicyInfo.setUserName(userJson.getString("name"));
            exportPolicyInfo.setPlateNo(elecJson.getString("plateNumber"));
            exportPolicyInfo.setPurchaseTime(elecJson.getDate("purchaseTime"));
            exportPolicyInfo.setElecType(elecJson.getString("model"));
            exportPolicyInfo.setVendor(elecJson.getString("vendor"));
            exportPolicyInfo.setVin(elecJson.getString("vin"));
            exportPolicyInfo.setImei(imei);
            exportPolicyInfo.setPolicyName(policyName);
            List<TuserInsurance> userInsurances = userInsuranceRepository.findByPolicyId(policyInfo.getId());
            if(userInsurances != null && !userInsurances.isEmpty()){
                List<Tinsurance> insurances = insuranceService.findByIdIn(userInsurances.stream().map(TuserInsurance::getInsuranceId).collect(Collectors.toList()));
                exportPolicyInfo.setInsurances(insurances);
            }
            result.add(exportPolicyInfo);
        }
        return result;
    }
    /**
     * 添加excel标题
     */
    private void addExcelTitle(ExportExcelData data) {
        List<String> titles = new ArrayList<>();
        titles.add("保单名称");
        titles.add("保险类型");
        titles.add("保险类型");
        titles.add("设备号");
        titles.add("姓名");
        titles.add("身份证号");
        titles.add("车牌号");
        titles.add("车架号");
        titles.add("购买日期");
        titles.add("电动车类型");
        titles.add("电动车品牌");
        data.setTitles(titles);
    }
    private ExportExcelData addExcelData(List<ExportPolicyInfo> list) {
        ExportExcelData data = new ExportExcelData();
        data.setName("保单统计");
        addExcelTitle(data);
        List<List<String>> rows = new LinkedList<>();
        if (list != null && list.size() > 0) {
            addExcelCellData(list, rows);
            log.info("export policy list size is:" + list.size());
            data.setRows(rows);
        }
        return data;
    }

    private void addExcelCellData(List<ExportPolicyInfo> list, List<List<String>> rows) {
        for (ExportPolicyInfo view : list) {
            List<String> row = new LinkedList<>();
            row.add(view.getPolicyName());
            List<Tinsurance> insurances = view.getInsurances();
            if(insurances != null && insurances.size() == 2){
                row.add(insurances.get(0).getName());
                row.add(insurances.get(1).getName());
            }else if(insurances != null && insurances.size() == 1){
                row.add(insurances.get(0).getName());
                row.add("");
            }else{
                row.add("");
                row.add("");
            }
            row.add(view.getImei());
            row.add(view.getUserName());
            row.add(view.getIdNumber());
            row.add(view.getPlateNo());
            row.add(view.getVin());
            if(view.getPurchaseTime() != null){
                row.add(view.getPurchaseTime().toString());
            }else{
                row.add("");
            }
            row.add(view.getElecType());
            row.add(view.getVendor());
            rows.add(row);
        }
    }

}
