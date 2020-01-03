package com.szhq.iemp.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.ExportExcelUtils;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.ExportExcelData;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.*;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.InsuranceService;
import com.szhq.iemp.device.api.service.OperatorService;
import com.szhq.iemp.device.api.service.PolicyInfoService;
import com.szhq.iemp.device.api.vo.DeviceVo;
import com.szhq.iemp.device.api.vo.ExportPolicyInfo;
import com.szhq.iemp.device.api.vo.PolicyInfo;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    @Autowired
    private OperatorService operatorService;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

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
            throw new NbiotException(3000007, "一个车辆只能关联一份保险");
        }
        policyInfo = findByImeiAndUserId(imei, userId);
        if(policyInfo == null){
            throw new NbiotException(3000008, "该设备未绑定保单");
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
            if(entity.getInsuranceIds().size() > 2){
                throw new NbiotException(3456, "保单激活功能暂未对IPHONE用户开放，请等待APP更新");
            }
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
    public List<TpolicyInfo> findByImeis(List<String> imeis) {
        return policyInfoRepository.findByImeis(imeis);
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
    public MyPage<DeviceVo> devices(Integer offset, Integer limit, DeviceQuery query) {
        List<DeviceVo> result = new ArrayList<>();
        Sort sort = SortUtil.sort("id","desc");
        if (sort == null) {
            throw new NbiotException(400, "");
        }
        List<Map<String, Object>> lists = null;
        Integer count = 0;
        Pageable pageable = PageRequest.of(offset, limit, sort);
        if(query != null){
            if(query.getOperatorIdList() != null && query.getOperatorIdList().get(0) != 0){
                lists = policyInfoRepository.all310devices(query.getOperatorIdList(), query.getImei(), pageable);
                count = policyInfoRepository.countAll310devices(query.getOperatorIdList(), query.getImei());
            }
            else {
                lists = policyInfoRepository.all310devices(query.getImei(), pageable);
                count = policyInfoRepository.countAll310devices(query.getImei());
            }
        }
        else{
            lists = policyInfoRepository.all310devices(pageable);
            count = policyInfoRepository.countAll310devices();
        }
        if(lists != null && !lists.isEmpty()){
            for(Map<String, Object> map : lists){
                DeviceVo deviceVo = new DeviceVo();
                String operatorName = (String)map.get("operator_name");
                String imei = (String)map.get("imei");
                String isp = (String)map.get("isp");
                String storehouseName = (String)map.get("storehouse_name");
                String ishaspolicy = (String)map.get("ishaspolicy");
                if(map.get("name_code") != null && !"null".equals(map.get("name_code"))){
                    Integer policyNameCode = (Integer)map.get("name_code");
                    String policyName = (String)map.get("name");
                    deviceVo.setPolicyNameCode(policyNameCode);
                    deviceVo.setPolicyName(policyName);
                }
                deviceVo.setImei(imei);
                deviceVo.setIsHavePolicy(Boolean.valueOf(ishaspolicy));
                deviceVo.setIsp(isp);
                deviceVo.setOperatorName(operatorName);
                deviceVo.setStorehouseName(storehouseName);
                result.add(deviceVo);
            }
        }
        return new MyPage<DeviceVo>(result, count, offset + 1, limit);
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
    public void batchSave(List<TpolicyInfo> entities) {
        int index = 0;
        for(TpolicyInfo tpolicyInfo : entities){
            if(tpolicyInfo.getId() != null){
                entityManager.merge(tpolicyInfo);
            }else{
                entityManager.persist(tpolicyInfo);
            }
            index++;
            if (index % 500 == 0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        if (index % 500 != 0){
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    public void batchUpdate(List<TpolicyInfo> entities) {
        int index = 0;
        for(TpolicyInfo tpolicyInfo : entities){
            entityManager.merge(tpolicyInfo);
            index++;
            if (index % 500 == 0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        if (index % 500 != 0){
            entityManager.flush();
            entityManager.clear();
        }
        return;
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
            policyInfo.setOperatorId(tpolicyInfo.getOperatorId());
            if(tpolicyInfo.getNameCode() != null){
                policyInfo.setName(tpolicyInfo.getName());
                policyInfo.setNameCode(tpolicyInfo.getNameCode());
            }
            save(policyInfo);
            return 1;
        }
        return 0;
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        policyInfoRepository.deleteByIds(ids);
    }

    @Override
    public Integer getLostPolicys() {
        List<Map<String, Object>> lists = policyInfoRepository.getLostPolicys();
        if(lists != null && !lists.isEmpty()){
            List<TpolicyInfo> result = new ArrayList<>();
            for(Map<String, Object> map : lists){
                String imei = (String)map.get("imei");
                TpolicyInfo tpolicyInfo = findByImei(imei);
                if(tpolicyInfo == null){
                    Integer operatorId = Integer.valueOf(String.valueOf(map.get("operator_id")));
                    String userId = (String)map.get("user_id");
                    TpolicyInfo policyInfo = new TpolicyInfo();
                    policyInfo.setOperatorId(operatorId);
                    policyInfo.setImei(imei);
                    policyInfo.setUserId(userId);
                    policyInfo.setNameCode(2);
                    policyInfo.setName("中国平安");
                    result.add(policyInfo);
                }

            }
            batchSave(result);
            return result.size();
        }
        return null;
    }

    @Override
    public Long initializePolicy(String imei) {
        TpolicyInfo policyInfo = findByImei(imei);
        if(policyInfo == null){
            log.error("device does not has policy.imei:{}", imei);
            throw new NbiotException(400027, "该设备不存在保单信息");
        }
        TdeviceInventory device = deviceInventoryService.findByImei(imei);
        if(device == null){
            log.error("device is not exist.imei:{}", imei);
            throw new NbiotException(400002, "设备不存在");
        }
        if(Objects.equals(1, device.getDevstate())){
            log.error("device has installed.can not initializePolicy.imei:{}", imei);
            throw new NbiotException(400026, "设备未解绑，不能初始化保单");
        }
        userInsuranceRepository.deleteByPolicyIds(Arrays.asList(policyInfo.getId()));
        policyInfo.setUserId(null);
        policyInfo.setActiveTime(null);
        policyInfo.setStartTime(null);
        policyInfo.setEndTime(null);
        policyInfo.setInfo(null);
        policyInfo.setIsEffective(false);
        policyInfo.setPlateNo(null);
        return save(policyInfo);
    }

    @Override
    public Long addNewPolicy(TpolicyInfo policyInfo) {
        String imei = policyInfo.getImei();
        TdeviceInventory device = deviceInventoryService.findByImei(imei);
        if(device == null){
            log.error("device is not exist. imei:{}", imei);
            throw new NbiotException(400002, "设备不存在");
        }
        TpolicyInfo policy = findByImei(imei);
        if(policy != null){
            log.error("policy has exist. imei:{}", imei);
            throw new NbiotException(400028, "该设备已有保单，不能再次新增保单");
        }
        policy = new TpolicyInfo();
        policy.setImei(imei);
        policy.setIsEffective(false);
        policy.setNameCode(policyInfo.getNameCode());
        policy.setName(policyInfo.getName());
        policy.setOperatorId(device.getOperatorId());
        return save(policy);
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
            Integer operatorId = policyInfo.getOperatorId();
            Toperator operator = operatorService.findById(operatorId);
            JSONObject infoJson = JSON.parseObject(info);
            JSONObject userJson = infoJson.getJSONObject("user");
            JSONObject elecJson = infoJson.getJSONObject("elec");
            exportPolicyInfo.setIdNumber(userJson.getString("idNumber"));
            exportPolicyInfo.setUserName(userJson.getString("name"));
            exportPolicyInfo.setPhone(userJson.getString("phone"));
            if("1".equals(userJson.getString("sex"))){
                exportPolicyInfo.setSex("男");
            }else {
                exportPolicyInfo.setSex("女");
            }
            if(operator != null){
                exportPolicyInfo.setOperatorName(operator.getName());
            }
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
        titles.add("运营公司");
        titles.add("保单名称");
        titles.add("保险类型");
        titles.add("保险类型");
        titles.add("设备号");
        titles.add("姓名");
        titles.add("电话");
        titles.add("性别");
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
            row.add(view.getOperatorName());
            row.add(view.getPolicyName());
            List<Tinsurance> insurances = view.getInsurances();
            if(insurances != null && insurances.size() == 2){
                row.add(insurances.get(0).getType());
                row.add(insurances.get(1).getType());
            }else if(insurances != null && insurances.size() == 1){
                row.add(insurances.get(0).getType());
                row.add("");
            }else{
                row.add("");
                row.add("");
            }
            row.add(view.getImei());
            row.add(view.getUserName());
            row.add(view.getPhone());
            row.add(view.getSex());
            row.add(view.getIdNumber());
            row.add(view.getPlateNo());
            row.add(view.getVin());
            if(view.getPurchaseTime() != null){
                row.add(TimeStampUtil.formatDate(view.getPurchaseTime(), "yyyy-MM-dd"));
            }else{
                row.add("");
            }
            row.add(view.getElecType());
            row.add(view.getVendor());
            rows.add(row);
        }
    }

}
