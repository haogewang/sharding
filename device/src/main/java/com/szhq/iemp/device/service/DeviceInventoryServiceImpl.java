package com.szhq.iemp.device.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.SiteExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.StorehouseExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DencryptTokenUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.*;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.api.vo.*;
import com.szhq.iemp.device.api.vo.enums.PolicyNameEnum;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.repository.DeviceDispachHistoryRepository;
import com.szhq.iemp.device.repository.DeviceInventoryRepository;
import com.szhq.iemp.device.util.RedisUtil;
import dot.server.api.DotService;
import iemp.nui.api.ContainerService;
import iemp.nui.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanghao
 * @date 2019/10/18
 */
@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "aepDeviceInventory")
public class DeviceInventoryServiceImpl implements DeviceInventoryService {

    @Resource
    private DeviceInventoryRepository deviceInventoryRepository;
    @Resource
    private DeviceDispachHistoryRepository dispachHistoryRepository;

    @Reference
    private ContainerService containerService;
    @Reference
    private DotService dotService;

    @Autowired
    private InstallSiteService installSiteService;
    @Autowired
    private DeviceStoreHouseService storeHouseService;
    @Autowired
    private DeviceDefectiveInventoryService defectiveInventoryService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ElectrmobileService elecTrmobileService;
    @Autowired
    private DeviceDispacheHistoryService dispacheHistoryService;
    @Autowired
    private DeviceManufactorService manufactorService;
    @Autowired
    private IotTypeService iotTypeService;
    @Autowired
    private AddressRegionService regionService;
    @Autowired
    private PolicyInfoService policyInfoService;
    @Autowired
    private UserService userService;
    @Autowired
    private SaleRecordService saleRecordService;
    @Autowired
    private ActiveInfoService activeInfoService;
    @Autowired
    private UserInsuranceService userInsuranceService;
    @Autowired
    private RedisUtil redisUtil;

    @Cacheable(unless="#result == null|| #result.getTotal() == 0")
    @Override
    public MyPage<TdeviceInventory> findAllByCriteria(Integer page, Integer size, String sorts, String orders, DeviceQuery deviceQuery,
                                                      Boolean isDispacher, Boolean isOutStore) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TdeviceInventory> pages = deviceInventoryRepository.findAll(new Specification<TdeviceInventory>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TdeviceInventory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (deviceQuery != null) {
                    if (deviceQuery.getStartTime() != null && deviceQuery.getEndTime() != null) {
                        list.add(criteriaBuilder.between(root.get("dispatchTime").as(Date.class), deviceQuery.getStartTime(), deviceQuery.getEndTime()));
                    }
                    if (deviceQuery.getDevstate() != null) {
                        list.add(criteriaBuilder.equal(root.get("devstate").as(Integer.class), deviceQuery.getDevstate()));
                    }
                    if (StringUtils.isNotEmpty(deviceQuery.getImei())) {
                        list.add(criteriaBuilder.equal(root.get("imei").as(String.class), deviceQuery.getImei()));
                    }
                    if (StringUtils.isNotEmpty(deviceQuery.getBoxNumber())) {
                        list.add(criteriaBuilder.equal(root.get("boxNumber").as(String.class), deviceQuery.getBoxNumber()));
                    }
                    if (deviceQuery.getInstallSiteId() != null) {
                        list.add(criteriaBuilder.equal(root.get("installSiteId").as(Integer.class), deviceQuery.getInstallSiteId()));
                    }
                    if (StringUtils.isNotEmpty(deviceQuery.getInstallSiteName())) {
                        list.add(criteriaBuilder.like(root.get("installSiteName").as(String.class), "%" + deviceQuery.getInstallSiteName() + "%"));
                    }
                    //出库列表使用
                    if (deviceQuery.getStorehouseId() != null) {
                        list.add(criteriaBuilder.notEqual(root.get("storehouseId").as(Integer.class), deviceQuery.getStorehouseId()));
                    }
                    if (deviceQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(deviceQuery.getOperatorIdList()));
                    }
                    if (StringUtils.isNotEmpty(deviceQuery.getIsp())) {
                        list.add(criteriaBuilder.equal(root.get("isp").as(String.class), deviceQuery.getIsp()));
                    }
                    if (deviceQuery.getGroupId() != null) {
                        list.add(criteriaBuilder.equal(root.get("groupId").as(Integer.class), deviceQuery.getGroupId()));
                    }
                    if (deviceQuery.getGroupIdList() != null) {
                        list.add(root.get("groupId").as(Integer.class).in(deviceQuery.getGroupIdList()));
                    }
                    if (deviceQuery.getStorehouseName() != null) {
                        list.add(criteriaBuilder.like(root.get("storehouseName").as(String.class), "%" + deviceQuery.getStorehouseName() + "%"));
                    }
                    if (deviceQuery.getOperatorName() != null) {
                        list.add(criteriaBuilder.like(root.get("operatorName").as(String.class), "%" + deviceQuery.getOperatorName() + "%"));
                    }
                    if (deviceQuery.getPutStorageStartTime() != null && deviceQuery.getPutStorageEndTime() != null) {
                        list.add(criteriaBuilder.between(root.get("putStorageTime").as(Date.class), deviceQuery.getPutStorageStartTime(), deviceQuery.getPutStorageEndTime()));
                    }
                }
                if (isDispacher != null && isDispacher) {
                    list.add(criteriaBuilder.isNull(root.get("installSiteId")));
                }
                if (isOutStore != null && isOutStore) {
                    list.add(criteriaBuilder.isNotNull(root.get("installSiteId")));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TdeviceInventory>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public List<TdeviceInventory> findAllByInstallSiteId(Integer siteId) {
        return deviceInventoryRepository.findAllByInstallSiteId(siteId);
    }

    @Override
    public TdeviceInventory save(TdeviceInventory entity) {
        return deviceInventoryRepository.save(entity);
    }

    @Override
    public List<TdeviceInventory> saveAll(List<TdeviceInventory> deviceList) {
        return deviceInventoryRepository.saveAll(deviceList);
    }

    @Override
    public TdeviceInventory findByImei(String imei) {
//        String deviceString = (String) redisUtil.get(CommonConstant.DEVICE_IMEI + imei);
//        if (deviceString == null || StringUtils.isEmpty(deviceString)) {
//            TdeviceInventory device = deviceInventoryRepository.findByImei(imei);
//            if (device != null) {
//                redisUtil.set(CommonConstant.DEVICE_IMEI + imei, JSONObject.toJSONString(device), 3600*24);
//            }
//            return device;
//        }
//        log.info("get device data from redis. imei:" + imei);
//        TdeviceInventory device = JSONObject.parseObject(deviceString, TdeviceInventory.class);
        TdeviceInventory device = deviceInventoryRepository.findByImei(imei);
        return device;
    }

    @Override
    public List<TdeviceInventory> findByImeiIn(List<String> imeiList) {
        return deviceInventoryRepository.findByImeiIn(imeiList);
    }

    @Override
    public Integer deleteByImei(String imei) {
//        commonService.deleteRedisByKey(CommonConstant.DEVICE_IMEI + imei);
        deleteRedisDeviceImeiByImei(imei);
        return deviceInventoryRepository.deleteByImei(imei);
    }

    private void deleteRedisDeviceImeiByImei(String imei) {
        if (redisUtil.hasKey(CommonConstant.DEVICE_IMEI + imei)) {
            redisUtil.del(CommonConstant.DEVICE_IMEI + imei);
            log.info("redis delete key [" + CommonConstant.DEVICE_IMEI + imei + "] success");
        }
    }

    @Override
    public Integer putinStorageByBoxNumbers(List<String> boxNumbers, Integer storehouseId, HttpServletRequest request) {
        List<String> boxNumberss = findByBoxNumberAndDevState(boxNumbers, 1);
        if(boxNumberss != null && !boxNumberss.isEmpty()){
            log.error("some devices already installed.");
            throw new NbiotException(400024, "箱号下有已安装设备", JSONObject.toJSONString(boxNumberss));
        }
        String putStorageUserId = DencryptTokenUtil.getUserId(request);
        TdeviceStoreHouse deviceStoreHouse = storeHouseService.findById(storehouseId);
        if (deviceStoreHouse == null) {
            log.error("storeHouse is not exist, storeHouseId is:" + storehouseId);
            throw new NbiotException(StorehouseExceptionEnum.E_00012.getCode(), StorehouseExceptionEnum.E_00012.getMessage());
        }
        if(deviceStoreHouse.getRegion() == null){
            log.error("storeHouse region is not exist, storeHouseId is:" + storehouseId);
            throw new NbiotException(400004, DeviceExceptionEnum.E_0002.getMessage());
        }
        Tinsurance insurance = null;
        if(deviceStoreHouse.getPolicyNameCode() != null){
            String policyName = PolicyNameEnum.getNameByCode(deviceStoreHouse.getPolicyNameCode());
            if(policyName == null){
                throw new NbiotException(404, StorehouseExceptionEnum.E_0007.getMessage());
            }
        }
        List<TdeviceInventory> list =  findByBoxNumbers(boxNumbers);
        if (list != null && list.size() > 0) {
            list.forEach(p -> {
                p.setStorehouseId(storehouseId);
                p.setStorehouseName(deviceStoreHouse.getName());
                p.setRegionId(deviceStoreHouse.getRegion().getId());
                p.setRegionName(deviceStoreHouse.getRegion().getAreaName());
                p.setPutStorageTime(new Date());
                p.setPutStorageUserId(putStorageUserId);
            });
            List<TdeviceInventory> results = new ArrayList<>();
            List<TpolicyInfo> policyInfos = new ArrayList<>();
            for (TdeviceInventory device: list) {
                if(!deviceStoreHouse.getIsActive()){
                    device.setIsActive(false);
                }
                if(deviceStoreHouse.getPolicyNameCode() != null){
                    TpolicyInfo policyInfo = new TpolicyInfo();
                    policyInfo.setImei(device.getImei());
                    policyInfo.setNameCode(deviceStoreHouse.getPolicyNameCode());
                    policyInfo.setName(PolicyNameEnum.getNameByCode(deviceStoreHouse.getPolicyNameCode()));
                    policyInfos.add(policyInfo);
                }
                Toperator operator = operatorService.findById(device.getOperatorId());
                if(operator != null){
                    device.setOperatorName(operator.getName());
                }
                results.add(device);
                deleteRedisDeviceImeiByImei(device.getImei());
            }
            saveAll(results);
            //写入保单表
            if(!policyInfos.isEmpty()){
                policyInfoService.saveAll(policyInfos);
            }
            deleteDeviceRedisData();
            broadcastTrdPlatform(boxNumbers);
        }
        return 0;
    }

    @Override
    public Integer putInStorageByDeliverSns(List<String> deliverSns, Integer storehouseId, HttpServletRequest request) {
        List<String> boxNumbers = findBoxNumbersByDeliverSns(deliverSns);
        return putinStorageByBoxNumbers(boxNumbers,storehouseId, request);
    }

    private List<String> findBoxNumbersByDeliverSns(List<String> deliverSns) {
        return deviceInventoryRepository.findBoxNumbersByDeliverSns(deliverSns);
    }

    private List<String> findByBoxNumberAndDevState(List<String> boxNumbers, int devstate) {
        return deviceInventoryRepository.findByBoxNumberAndDevState(boxNumbers, devstate);
    }

    @Override
    public Integer dispatchByImeis(List<String> imeis, Integer installSiteId) {
        TinstallSite installSite = installSiteService.findById(installSiteId);
        if (installSite == null) {
            log.error("installSite is not exist, installSiteId is:" + installSiteId);
            throw new NbiotException(SiteExceptionEnum.E_0005.getCode(), SiteExceptionEnum.E_0005.getMessage());
        }
        List<TdeviceInventory> list = findByImeiIn(imeis);
        if (list != null && list.size() > 0) {
            list.forEach(p -> {
                p.setInstallSiteId(installSiteId);
                p.setInstallSiteName(installSite.getName());
                p.setDispatchTime(new Date());
            });
            saveAll(list);
            saveDeviceDispacheLog(list);
            deleteDeviceRedisData();
            for(String imei : imeis){
                deleteRedisDeviceImeiByImei(imei);
            }
        }
        return 0;
    }

    @Override
    public Integer dispatchByBoxNumber(List<String> boxNumbers, Integer installSiteId) {
        List<TdeviceInventory> devices = findByBoxNumbers(boxNumbers);
        if(devices == null){
            log.error("device is not exist.boxNumbers:" + JSONObject.toJSONString(boxNumbers));
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
        List<String> imeis = devices.stream().map(TdeviceInventory::getImei).collect(Collectors.toList());
        dispatchByImeis(imeis, installSiteId);
        return devices.size();
    }

    @Override
    public List<PutStorageCount> putStorageStatistic(Integer offset, Integer limit, DeviceQuery query) {
        List<PutStorageCount> counts = new ArrayList<>();
        Sort sort = SortUtil.sort("put_storage_time", "desc");
        Pageable pageable = PageRequest.of(offset, limit, sort);
        List<Map<String, Object>> lists = null;
        if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() == null) {
            lists = deviceInventoryRepository.putStorageStatistic(query.getStartTime(), query.getEndTime(), query.getDevType(), pageable);
        }
        else if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.putStorageStatistic(query.getStartTime(), query.getEndTime(), query.getOperatorIdList(), query.getDevType(), pageable);
        }
        else if (query != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.putStorageStatistic(query.getOperatorIdList(), query.getDevType(), pageable);
        }
        else if (query != null && query.getStartTime() != null && query.getEndTime() != null) {
            lists = deviceInventoryRepository.putStorageStatistic(query.getStartTime(), query.getEndTime(), query.getDevType(), pageable);
        }
        else {
            lists = deviceInventoryRepository.putStorageStatistic(query.getDevType(), pageable);
        }
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                PutStorageCount putStorageCount = new PutStorageCount();
                Long boxCount = Long.valueOf(map.get("box_count").toString());
                Long imeiCount = Long.valueOf(map.get("imei_count").toString());
                String createTime = (String) map.get("put_storage_time");
                Long cmccCount = Long.valueOf(map.get("cmcc_count").toString());
                Long ctCount = Long.valueOf(map.get("ct_count").toString());
                putStorageCount.setBoxCount(boxCount);
                if(StringUtils.isNotEmpty(createTime)){
                    putStorageCount.setCreateTime(TimeStampUtil.parseDate(createTime, "yyyy-MM-dd"));
                }
                putStorageCount.setImeiCount(imeiCount);
                putStorageCount.setCmccCount(cmccCount);
                putStorageCount.setCtCount(ctCount);
                counts.add(putStorageCount);
            }
        }
        return counts;
    }

    @Override
    public List<DispachCount> dispatchStatistic(Integer offset, Integer limit, DeviceQuery query) {
        List<DispachCount> counts = new ArrayList<>();
        Sort sort = SortUtil.sort("dispatch_time", "desc");
        List<Map<String, Object>> lists = new ArrayList<>();
        Pageable pageable = PageRequest.of(offset, limit, sort);
        if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() == null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getInstallSiteName(), query.getDevType(), pageable);
        }
        else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getInstallSiteName(), query.getOperatorIdList(), query.getDevType(), pageable);
        }
        else if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() == null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getStartTime(), query.getEndTime(), query.getDevType(),pageable);
        }
        else if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getStartTime(), query.getEndTime(), query.getOperatorIdList(), query.getDevType(),pageable);
        }
        else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() == null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getInstallSiteName(), query.getStartTime(), query.getEndTime(), query.getDevType(), pageable);
        }
        else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getInstallSiteName(), query.getStartTime(), query.getEndTime(), query.getOperatorIdList(),query.getDevType(), pageable);
        }
        else if (query != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.dispatchStatistic(query.getOperatorIdList(),query.getDevType(), pageable);
        }
        else {
            lists = deviceInventoryRepository.dispatchStatistic(query.getDevType(),pageable);
        }
        if (lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                DispachCount dispachCount = new DispachCount();
                Long installSiteId = null;
                if (map.get("install_site_id") != null) {
                    installSiteId = Long.valueOf(map.get("install_site_id").toString());
                }
                String installSiteName = (String) map.get("install_site_name");
                Long totalCount = Long.valueOf(map.get("total_count").toString());
                Long CTcount = Long.valueOf(map.get("ct_count").toString());
                Long CUCCcount = Long.valueOf(map.get("cucc_count").toString());
                Long CMCCcount = Long.valueOf(map.get("cmcc_count").toString());
                Date dispatchTime = (Date) map.get("dispatch_time");
                setDispatchCount(counts, dispachCount, installSiteId, installSiteName, totalCount, CTcount, CMCCcount,CUCCcount, dispatchTime);
            }
        }
        return counts;
    }

    @Override
    public List<DispachCount> backOffStatistic(Integer offset, Integer limit, DeviceQuery query) {
        List<DispachCount> counts = new ArrayList<>();
        Sort sort = SortUtil.sort("create_time", "desc");
        List<Map<String, Object>> lists = new ArrayList<>();
        Pageable pageable = PageRequest.of(offset, limit, sort);
        if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() == null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getInstallSiteName(), pageable);
        }
        else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() != null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getInstallSiteName(), query.getOperatorIdList(), pageable);
        }
        else if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() == null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getStartTime(), query.getEndTime(), pageable);
        }
        else if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() != null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getStartTime(), query.getEndTime(), query.getOperatorIdList(), pageable);
        }
        else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() == null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getInstallSiteName(), query.getStartTime(), query.getEndTime(), pageable);
        }
        else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() != null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getInstallSiteName(), query.getStartTime(), query.getEndTime(), query.getOperatorIdList(), pageable);
        }
        else if (query != null && query.getOperatorIdList() != null) {
            lists = dispachHistoryRepository.backOffStatistic(query.getOperatorIdList(), pageable);
        }
        else {
            lists = dispachHistoryRepository.backOffStatistic(pageable);
        }
        if (lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                DispachCount dispachCount = new DispachCount();
                Long installSiteId = null;
                if (map.get("oldsite_id") != null) {
                    installSiteId = Long.valueOf(map.get("oldsite_id").toString());
                }
                String installSiteName = (String) map.get("oldsite_name");
                Long totalCount = Long.valueOf(map.get("total_count").toString());
                Long CTcount = Long.valueOf(map.get("ct_count").toString());
                Long CMCCcount = Long.valueOf(map.get("cmcc_count").toString());
                Long CUCCcount = Long.valueOf(map.get("cucc_count").toString());
                Date dispatchTime = (Date) map.get("create_time");
                setDispatchCount(counts, dispachCount, installSiteId, installSiteName, totalCount, CTcount, CMCCcount,CUCCcount, dispatchTime);
            }
        }
        return counts;
    }

    @Override
    public List<UnDispacheDeviceCount> getUndispacheDeviceCountOfIsp(DeviceQuery query) {
        List<UnDispacheDeviceCount> counts = new ArrayList<>();
        List<Map<String, Object>> lists = new ArrayList<>();
        if (query != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.getUndispacheDeviceCountOfIsp(query.getOperatorIdList());
        }
        else {
            lists = deviceInventoryRepository.getUndispacheDeviceCountOfIsp();
        }
        if (lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                UnDispacheDeviceCount unDispacheDeviceCount = new UnDispacheDeviceCount();
                Long count = Long.valueOf(map.get("counts").toString());
                String isp = (String) map.get("isp");
                unDispacheDeviceCount.setCount(count);
                unDispacheDeviceCount.setIsp(isp);
                counts.add(unDispacheDeviceCount);
            }
        }
        return counts;
    }

    @Override
    public Long countByCriteria(DeviceQuery deviceQuery) {
        Long count = deviceInventoryRepository.count(new Specification<TdeviceInventory>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TdeviceInventory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (deviceQuery != null) {
                    if (deviceQuery.getInstallSiteId() != null) {
                        list.add(criteriaBuilder.equal(root.get("installSiteId").as(Integer.class), deviceQuery.getInstallSiteId()));
                    }
                    if (deviceQuery.getDevstate() != null) {
                        list.add(criteriaBuilder.equal(root.get("devstate").as(Integer.class), deviceQuery.getDevstate()));
                    }
                    if (StringUtils.isNotEmpty(deviceQuery.getIsp())) {
                        list.add(criteriaBuilder.equal(root.get("isp").as(String.class), deviceQuery.getIsp()));
                    }
                    if (StringUtils.isNotEmpty(deviceQuery.getInstallSiteName())) {
                        list.add(criteriaBuilder.equal(root.get("installSiteName").as(String.class), deviceQuery.getInstallSiteName()));
                    }
                    if (deviceQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(deviceQuery.getOperatorIdList()));
                    }
                    if (deviceQuery.getIsDispache() != null && !deviceQuery.getIsDispache()) {
                        list.add(criteriaBuilder.isNull(root.get("installSiteId")));
                    }
                    if (deviceQuery.getIsDispache() != null && deviceQuery.getIsDispache()) {
                        list.add(criteriaBuilder.isNotNull(root.get("installSiteId")));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return count;
    }

    @Override
    public Integer getDeviceNumByInstallSiteId(Integer id) {
        return deviceInventoryRepository.getDeviceNumByInstallSiteId(id);
    }

    @Override
    public Integer countByGroupId(Integer groupId) {
        return deviceInventoryRepository.countByGroupId(groupId);
    }

    @Override
    public Integer countByGroupIds(List<Integer> groupIds) {
        return deviceInventoryRepository.countByGroupIds(groupIds);
    }

    @Override
    public Long countByOperatorIds(List<Integer> ids) {
        return  deviceInventoryRepository.getDeviceNumByOperatorIds(ids);
    }


    @Override
    public Long countByStoreHouseId(Integer id) {
        return deviceInventoryRepository.countByStoreHouseId(id);
    }

    @Override
    public Long countByStoreHouseIds(List<Integer> subStoreIds) {
        return deviceInventoryRepository.countByStoreHouseIds(subStoreIds);
    }

    @Override
    public Integer countUnActiveByOperatorIds(List<Integer> operatorIds) {
        return deviceInventoryRepository.countUnActiveByOperatorIds(operatorIds);
    }

    @Override
    public List<DispachCount> countHistoryInstalledUnormalCount(DeviceQuery query) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (query != null && query.getOperatorIdList() != null) {
            list = deviceInventoryRepository.countHistoryInstalledUnormalCount(query.getOperatorIdList());
        } else {
            list = deviceInventoryRepository.countHistoryInstalledUnormalCount();
        }
        List<DispachCount> counts = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                DispachCount dispachCount = new DispachCount();
                Long totals = Long.valueOf(map.get("totals").toString());
                Long CTCount = Long.valueOf(map.get("CTcount").toString());
                Long CMCCCount = Long.valueOf(map.get("CMCCcount").toString());
                String installSiteName = (String) map.get("name");
                dispachCount.setCmccCount(CMCCCount);
                dispachCount.setCtCount(CTCount);
                dispachCount.setTotalCount(totals);
                dispachCount.setInstallSiteName(installSiteName);
                counts.add(dispachCount);
            }
        }
        return counts;
    }

    @Override
    public List<DeviceCount> countHistoryInstalledByOffset(Integer offset, DeviceQuery query) {
        List<Map<String, Object>> lists = null;
        if (query != null && query.getOperatorIdList() != null) {
            lists = deviceInventoryRepository.countHistoryInstalledByOffset(offset, query.getOperatorIdList());
        } else {
            lists = deviceInventoryRepository.countHistoryInstalledByOffset(offset);
        }
        List<DeviceCount> deviceCounts = new ArrayList<>();
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                DeviceCount deviceCount = new DeviceCount();
                Date date = (Date) map.get("time");
                long count = Long.valueOf(String.valueOf(map.get("counts")));
                deviceCount.setCount(count);
                deviceCount.setDate(date);
                deviceCounts.add(deviceCount);
            }
        }
        return deviceCounts;
    }

    @Override
    public Integer updateActiveStateByImei(String imei, Boolean status) {
        TdeviceInventory device = findByImei(imei);
        if(device != null){
            device.setIsActive(status);
            save(device);
            deleteRedisDeviceImeiByImei(imei);
            return 1;
        }
        return 0;
    }

    @Override
    public Integer updateActiveStateByImeis(List<String> imeis, Boolean status) {
        Integer count = deviceInventoryRepository.updateActiveStateByImeis(imeis, status);
        for(String imei : imeis){
            deleteRedisDeviceImeiByImei(imei);
        }
        log.info("update active count:" + count);
        return count;
    }

    @Override
    public Map<String, List<DeviceOfBox>> getBoxNumberByPutStorageTime(DeviceQuery query) {
        List<DeviceOfBox> ctCounts = new ArrayList<>();
        List<DeviceOfBox> cmccCounts = new ArrayList<>();
        List<DeviceOfBox> cuccCounts = new ArrayList<>();
        List<Map<String, Object>> lists = new ArrayList<>();
        if (query != null && query.getPutStorageTime() != null) {
            LocalDate localDate = query.getPutStorageTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Date startDate = new Date(java.sql.Date.valueOf(localDate).getTime());
            localDate = localDate.plusDays(1);
            Date endDate = new Date(java.sql.Date.valueOf(localDate).getTime() - 1L);
            log.info("start:" + startDate + ",end:" + endDate + ",operatorIds:" + JSONObject.toJSONString(query.getOperatorIdList()));
            if (query.getOperatorIdList() != null) {
                lists = deviceInventoryRepository.getBoxNumberByPutStorageTime(startDate, endDate, query.getOperatorIdList());
            } else {
                lists = deviceInventoryRepository.getBoxNumberByPutStorageTime(startDate, endDate);
            }
        }
        if (lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                DeviceOfBox deviceOfBox = new DeviceOfBox();
                String putStorageUserId = (String) map.get("put_storage_user_id");
                String boxNumber = (String) map.get("box_number");
                Long imeiCount = Long.valueOf(map.get("total_count").toString());
                String isp = (String) map.get("isp");
                if(StringUtils.isNotEmpty(putStorageUserId)){
                    Tuser user = userService.findById(putStorageUserId);
                    if(user != null){
                        deviceOfBox.setPutStorageUserName(user.getName());
                    }else{
                        log.error("user not found.userId:" + putStorageUserId);
                        deviceOfBox.setPutStorageUserName("");
                    }
                }else {
                    deviceOfBox.setPutStorageUserName("");
                }
                deviceOfBox.setBoxDeviceNumber(imeiCount);
                deviceOfBox.setBoxNumber(boxNumber);
                deviceOfBox.setIsp(isp);
                setISPValue(ctCounts, cmccCounts, cuccCounts, deviceOfBox, isp);
            }
        }
        Map<String, List<DeviceOfBox>> map = new HashMap<>();
        map.put(CommonConstant.CT, ctCounts);
        map.put(CommonConstant.CMCC, cmccCounts);
        map.put(CommonConstant.CUCC, cuccCounts);
        return map;
    }

    @Override
    public Map<String, List<DeviceOfBox>> getBackOffBoxNumbers(DeviceQuery query) {
        List<DeviceOfBox> ctCounts = new ArrayList<>();
        List<DeviceOfBox> cmccCounts = new ArrayList<>();
        List<DeviceOfBox> cuccCounts = new ArrayList<>();
        List<Integer> operatorIds = new ArrayList<>();
        List<Map<String, Object>> lists = null;
        if (query != null && query.getOperatorIdList() != null) {
            operatorIds = query.getOperatorIdList();
            lists = deviceInventoryRepository.getBackOffBoxNumbers(operatorIds);
        } else {
            lists = deviceInventoryRepository.getBackOffBoxNumbers();
        }
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                DeviceOfBox deviceOfBox = new DeviceOfBox();
                String boxNumber = (String) map.get("box_number");
                String isp = (String) map.get("isp");
                Long totalCount = Long.valueOf(map.get("total_count").toString());
                List<Map<String, Object>> siteList = deviceInventoryRepository.getInstallSiteAndCountByBoxNumber(boxNumber);
                //验证同一箱子设备是否在同一安装点
                if (siteList != null && siteList.size() > 1) {
                    log.error("boxNumber: {} devices are not in one installSite.", boxNumber);
                    continue;
                }
                deviceOfBox.setIsp(isp);
                deviceOfBox.setBoxNumber(boxNumber);
                deviceOfBox.setBoxDeviceNumber(totalCount);
                setISPValue(ctCounts, cmccCounts, cuccCounts, deviceOfBox, isp);
            }
        }
        Map<String, List<DeviceOfBox>> map = new HashMap<>();
        map.put(CommonConstant.CT, ctCounts);
        map.put(CommonConstant.CMCC, cmccCounts);
        map.put(CommonConstant.CUCC, cuccCounts);
        return map;
    }

    @Override
    public DeviceOfBox validPutStorageByBoxNumber(String boxNumber) {
        List<TdeviceInventory> devices = findByBoxNumber(boxNumber);
        if (devices == null || devices.size() == 0) {
            log.error("boxNumber is not exist.boxNumber:" + boxNumber);
            throw new NbiotException(400006, DeviceExceptionEnum.E_00013.getMessage());
        }
        List<String> ispList = devices.stream().map(TdeviceInventory::getIsp).distinct().collect(Collectors.toList());
        if(!ispList.isEmpty() && ispList.size() > 1){
            log.error("the boxNumber devices has different isp.please check.boxNumber:{},isp:{}", boxNumber, JSONObject.toJSONString(ispList));
            throw new NbiotException(400017, DeviceExceptionEnum.E_00031.getMessage() + JSONObject.toJSONString(ispList));
        }
        for (TdeviceInventory device : devices) {
            if (device.getInstallSiteId() != null) {
                log.error("device has dispatched.siteName:" + device.getInstallSiteName());
                throw new NbiotException(400010, DeviceExceptionEnum.E_00010.getMessage());
            }
            if (Objects.equals(1, device.getDevstate())) {
                log.error("imei:" + device.getImei() + " has installed. devstate:" + device.getDevstate());
                throw new NbiotException(400010, DeviceExceptionEnum.E_00010.getMessage());
            }
        }
        DeviceOfBox deviceOfBox = new DeviceOfBox();
        deviceOfBox.setIsp(ispList.get(0));
        deviceOfBox.setBoxDeviceNumber((long) devices.size());
        deviceOfBox.setBoxNumber(boxNumber);
        return deviceOfBox;
    }

    @Override
    public TdeviceInventory validImeiInfo(String imei) {
        TdeviceInventory deviceInventory = findByImei(imei);
        if (deviceInventory == null) {
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
        if (deviceInventory.getInstallSiteId() != null) {
            throw new NbiotException(400010, DeviceExceptionEnum.E_00010.getMessage());
        }
        if (deviceInventory.getStorehouseId() != null && Objects.equals(1, deviceInventory.getStorehouseId())) {
            throw new NbiotException(400009, DeviceExceptionEnum.E_0009.getMessage());
        }
        return deviceInventory;
    }

    @Override
    public List<String> validBackoffInfoByBoxNumbers(List<String> boxNumbers) {
        List<TdeviceInventory> devices = findByBoxNumbers(boxNumbers);
        if (devices == null || devices.isEmpty()) {
            log.error("boxNumber is not exist.boxNumber:" + boxNumbers);
            throw new NbiotException(400006, DeviceExceptionEnum.E_00013.getMessage());
        }
        for (TdeviceInventory device : devices) {
            validBackoffInfo(device);
        }
        return boxNumbers;
    }

    @Override
    public TdeviceInventory validBackoffInfoByImei(String imei) {
        TdeviceInventory device = findByImei(imei);
        validBackoffInfo(device);
        return device;
    }

    @Override
    public TdeviceInventory validReturnOffInfoByImei(String imei) {
        TdeviceInventory device = findByImei(imei);
        validReturnOffDeviceInfo(device);
        return device;
    }

    @Override
    public Integer backOffByBoxNumbers(List<String> boxNumbers) {
        List<String> imeis = deviceInventoryRepository.getBackOffImeisByBoxNumbers(boxNumbers);
        Integer result = backOffByImeis(imeis);
        return result;
    }

    @Override
    public Integer backOffByImeis(List<String> imeis) {
        List<TdeviceInventory> devices = findByImeiIn(imeis);
        if (devices == null) {
            log.error("device is not exist, imeis is:" + JSONObject.toJSONString(imeis));
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
        List<TdeviceInventory> result = new ArrayList<>();
        for (TdeviceInventory device : devices) {
            if(Objects.equals(1, device.getDevstate())){
                log.error("device has installed.can not backoff.imei:" + device.getImei());
                throw new NbiotException(400019, "", device.getImei());
            }
            TinstallSite site = installSiteService.findById(device.getInstallSiteId());
            if (site != null) {
                log.info("installSite is not null.imei:" + device.getImei());
                device.setInstallSiteId(null);
                device.setInstallSiteName(null);
            }
            else {
                log.info("installSite is null.imei:" + device.getImei());
                TdeviceStoreHouse deviceStoreHouse = storeHouseService.findById(device.getStorehouseId());
                if (deviceStoreHouse != null) {
                    TdeviceStoreHouse parentStoreHouse = deviceStoreHouse.getParent();
                    if (parentStoreHouse == null) {
                        device.setStorehouseId(1);
                        device.setStorehouseName(storeHouseService.findById(1).getName());
                    } else {
                        device.setStorehouseId(parentStoreHouse.getId());
                        device.setStorehouseName(parentStoreHouse.getName());
                    }
                }
            }
            result.add(device);
            saveBackOffDispacheLog(device, site);
            deleteRedisDeviceImeiByImei(device.getImei());
        }
        saveAll(result);
        deleteNoInstalledPolicyByImeis(imeis);
        deleteDeviceRedisData();
        return result.size();
    }

    @Override
    public Integer backToHQByBoxNumber(List<String> boxNumbers) {
        List<TdeviceInventory> devices = findByBoxNumbers(boxNumbers);
        if(devices == null || devices.isEmpty()){
            log.error("device is not found.");
            throw new NbiotException(400002, "device is not found.");
        }
        List<Integer> devstates = devices.stream().map(TdeviceInventory::getDevstate).distinct().collect(Collectors.toList());
        if(devstates.contains(1)){
            log.error("devices has bound.can not backoff to HQ");
            throw new NbiotException(400019, "devices has bound");
        }
        List<String> imeis = devices.stream().map(TdeviceInventory::getImei).distinct().collect(Collectors.toList());
        List<TpolicyInfo> policys = policyInfoService.findByImeis(imeis);
        if(policys != null && !policys.isEmpty()){
            List<Long> ids = policys.stream().map(TpolicyInfo::getId).collect(Collectors.toList());
            userInsuranceService.deleteByPolicyIds(ids);
            policyInfoService.deleteByIds(ids);
            log.info("delete policy,imei:" + JSONObject.toJSONString(imeis));
        }
        Integer count = updateStorehouseToHQByImeis(imeis);
        deleteDeviceRedisData();
        log.info("return to HQ count:" + count);
        for(String boxNumber : boxNumbers){
            JSONObject json = new JSONObject();
            json.put("container_sn", boxNumber);
            Result result = containerService.deviceUnreceive(json);
            log.info("3rd deviceUnReceive json parameter:" + json + ",result:" + result);
            if (Objects.equals(result.getCode(), 0)){
                log.error("3rd deviceUnReceive error.result:" + result);
                throw new NbiotException(400025, "通知第三方平台退库失败");
            }
        }
        for(String imei : imeis){
            deleteRedisDeviceImeiByImei(imei);
        }
        return boxNumbers.size();
    }

    @Override
    public Integer backToHQByImeis(List<String> imeis) {
        List<TdeviceInventory> devices = findByImeiIn(imeis);
        if(devices == null || devices.isEmpty()){
            log.error("device is not exist.");
            throw new NbiotException(400002, "设备不存在");
        }
        List<Integer> devstates = devices.stream().map(TdeviceInventory::getDevstate).distinct().collect(Collectors.toList());
        if(devstates.contains(1)){
            throw new NbiotException(400011, "设备已安装");
        }
        List<Integer> siteIds = devices.stream().map(TdeviceInventory::getInstallSiteId).collect(Collectors.toList());
        if(!siteIds.isEmpty()){
            throw new NbiotException(400010, "设备已分配");
        }
        deleteNoInstalledPolicyByImeis(imeis);
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for(String imei : imeis){
            array.add(imei);
            deleteByImei(imei);
        }
        json.put("imei_list", array);
        log.info("device Undilver parameter:{}",json);
        try {
            containerService.deviceUndilver(json);
        } catch (Exception e) {
            log.error("e", e);
            throw new NbiotException(503, "调用第三方接口失败");
        }
        return imeis.size();
    }

    private Integer updateStorehouseToHQByImeis(List<String> imeis) {
        return deviceInventoryRepository.updateStorehouseToHQByImeis(imeis);
    }

    @Override
    public Integer returnOffDeviceByImeis(List<String> imeis) {
        List<TdeviceInventory> devices = findByImeiIn(imeis);
        if(devices == null || devices.isEmpty()){
            log.error("imei is not found.imeis:" + JSONObject.toJSONString(imeis));
            throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
        }
        for(TdeviceInventory device : devices){
            validReturnOffDeviceInfo(device);
            TdeviceDefectiveInventory defectiveInventory = structorDefectiveData(device);
            defectiveInventoryService.save(defectiveInventory);
            deleteByImei(device.getImei());
        }
        deleteDeviceRedisData();
        return devices.size();
    }

    @Override
    public Integer defectiveToNormalByImeis(List<String> imeis) {
        int count = 0;
        for (String imei : imeis){
            TdeviceDefectiveInventory defectiveInventory = defectiveInventoryService.findByImei(imei);
            TdeviceInventory tdeviceInventory = new TdeviceInventory();
            BeanUtils.copyProperties(defectiveInventory, tdeviceInventory);
            log.info("defectiveToNormal entity:" + JSONObject.toJSONString(tdeviceInventory));
            save(tdeviceInventory);
            deleteRedisDeviceImeiByImei(imei);
            int i = defectiveInventoryService.deleteByImei(imei);
            log.info("delete defective imei:{},count:{}",imei, i);
            count ++;
        }
        return count;
    }

    @Override
    public List<Integer> getOperatorIdsByBoxNumbers(List<String> boxNumbers) {
        return deviceInventoryRepository.getOperatorIdsByBoxNumbers(boxNumbers);
    }

    @Override
    public List<Integer> getOperatorIdsByDeliverSns(List<String> deliverSns) {
        return deviceInventoryRepository.getOperatorIdsByDeliverSns(deliverSns);
    }

    @Override
    public List<Integer> getOperatorIdsByImeis(List<String> imeis) {
        List<Integer> operatorIds = deviceInventoryRepository.getOperatorIdsByImeis(imeis);
        return operatorIds;
    }

    @Override
    public Integer getDeviceNumberByOperatorId(Integer id) {
        return elecTrmobileService.getDeviceNumberByOperatorId(id);
    }

    @Override
    public Integer importDevice(List<TdeviceInventory> deviceList) {
        List<TdeviceInventory> result = new ArrayList<>();
        List<String> imeis = deviceList.stream().map(TdeviceInventory::getImei).collect(Collectors.toList());
        List<TdeviceInventory> oldDevices = findByImeiIn(imeis);
        if(oldDevices != null && !oldDevices.isEmpty()){
            List<String> existImeis = oldDevices.stream().map(TdeviceInventory::getImei).collect(Collectors.toList());
            String existImeiString = JSONObject.toJSONString(existImeis);
            log.error("import device failed." + existImeiString + " already exist!");
            throw new NbiotException(DeviceExceptionEnum.E_0007.getCode(), existImeiString + DeviceExceptionEnum.E_0007.getMessage());
        }
        for(TdeviceInventory device : deviceList){
            TiotType iotType = iotTypeService.findById(device.getIotTypeId());
            if(iotType == null){
                log.error("iotType is not exist.id:" + device.getIotTypeId());
                throw new NbiotException(400, DeviceExceptionEnum.E_0004.getMessage());
            }
            TdeviceManufactor manufactor = manufactorService.findById(device.getManufactorId());
            if(manufactor == null){
                log.error("manufactor is not exist.id:" + device.getManufactorId());
                throw new NbiotException(400, DeviceExceptionEnum.E_0001.getMessage());
            }
            TdeviceStoreHouse storeHouse = storeHouseService.findById(device.getStorehouseId());
            if(storeHouse == null){
                log.error("storeHouse is not exist.id:" + device.getStorehouseId());
                throw new NbiotException(400, StorehouseExceptionEnum.E_00012.getMessage());
            }
            Toperator operator = operatorService.findById(device.getOperatorId());
            if(operator == null){
                log.error("operator is not exist.id:" + device.getOperatorId());
                throw new NbiotException(400, OperatorExceptionEnum.E_0002.getMessage());
            }
            TaddressRegion region = regionService.findById(device.getRegionId());
            if(region == null){
                log.error("region is not exist.id:" + device.getRegionId());
                throw new NbiotException(400, DeviceExceptionEnum.E_0002.getMessage());
            }
            device.setIotTypeName(iotType.getName());
            device.setManufactorName(manufactor.getName());
            device.setStorehouseName(storeHouse.getName());
            device.setOperatorName(operator.getName());
            device.setRegionName(region.getAreaName());
            result.add(device);
        }
        result = saveAll(result);
        return result.size();
    }

    private void setISPValue(List<DeviceOfBox> ctCounts, List<DeviceOfBox> cmccCounts, List<DeviceOfBox> cuccCounts, DeviceOfBox deviceOfBox, String isp) {
        if (CommonConstant.CT.equalsIgnoreCase(isp)) {
            ctCounts.add(deviceOfBox);
        } else if (CommonConstant.CMCC.equalsIgnoreCase(isp)) {
            cmccCounts.add(deviceOfBox);
        } else if (CommonConstant.CUCC.equalsIgnoreCase(isp)) {
            cuccCounts.add(deviceOfBox);
        }
    }

    private void deleteNoInstalledPolicyByImeis(List<String> imeis) {
        Integer count = policyInfoService.deleteNoInstalledPolicyByImeis(imeis);
        log.info("delete policy count:" + count);
    }

    /**
     *统计赋值
     */
    private void setDispatchCount(List<DispachCount> counts, DispachCount dispachCount, Long installSiteId, String installSiteName, Long totalCount, Long CTcount, Long CMCCcount, Long CUCCcount, Date dispatchTime) {
        dispachCount.setCmccCount(CMCCcount);
        dispachCount.setCtCount(CTcount);
        dispachCount.setCuccCount(CUCCcount);
        dispachCount.setDispatchTime(dispatchTime);
        dispachCount.setInstallSiteId(installSiteId);
        dispachCount.setInstallSiteName(installSiteName);
        dispachCount.setTotalCount(totalCount);
        counts.add(dispachCount);
    }
    /**
     * 验证退货设备信息
     */
    private void validReturnOffDeviceInfo(TdeviceInventory deviceInventory) {
        if (deviceInventory == null) {
            throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
        }
        if (Objects.equals(1, deviceInventory.getStorehouseId())) {
            log.error("device storehouse id is 1.imei:" + deviceInventory.getImei());
            throw new NbiotException(DeviceExceptionEnum.E_00012.getCode(), DeviceExceptionEnum.E_00012.getMessage());
        }
        if (deviceInventory.getDevstate() != null && Objects.equals(1, deviceInventory.getDevstate())) {
            log.error("device already installed.imei:" + deviceInventory.getImei());
            throw new NbiotException(DeviceExceptionEnum.E_00010.getCode(), DeviceExceptionEnum.E_00010.getMessage());
        }
    }
    /**
     * 保存设备分配记录
     */
    private void saveDeviceDispacheLog(List<TdeviceInventory> list) {
        List<TdeviceDispatchHistory> result = new ArrayList<>();
        for(TdeviceInventory device : list){
            TdeviceDispatchHistory dispatchHistory = new TdeviceDispatchHistory();
            dispatchHistory.setOldsiteId(null);
            dispatchHistory.setNewsiteId(device.getInstallSiteId());
            String deviceStringJson = JSONObject.toJSONString(device);
//            log.error("json:" + deviceStringJson);
            dispatchHistory.setDeviceNew(deviceStringJson);
            dispatchHistory.setImei(device.getImei());
            dispatchHistory.setSnNo(device.getSnNo());
            dispatchHistory.setBoxNumber(device.getBoxNumber());
            dispatchHistory.setOperatorId(device.getOperatorId());
            dispatchHistory.setOperatorName(device.getOperatorName());
            dispatchHistory.setNewsiteName(device.getInstallSiteName());
            dispatchHistory.setIsp(device.getIsp());
            dispatchHistory.setStoreHouse(storeHouseService.findById(device.getStorehouseId()));
            dispatchHistory.setRegion(regionService.findById(device.getRegionId()));
            dispatchHistory.setManufactor(manufactorService.findById(device.getManufactorId()));
            dispatchHistory.setIotType(iotTypeService.findById(device.getIotTypeId()));
            dispatchHistory.setType(CommonConstant.DISPATCH);
            result.add(dispatchHistory);
        }
        dispacheHistoryService.saveAll(result);
    }

    /**
     * 保存退库记录
     */
    private void saveBackOffDispacheLog(TdeviceInventory device, TinstallSite site) {
        TdeviceDispatchHistory dispatchHistory = new TdeviceDispatchHistory();
        TdeviceStoreHouse storehouse = storeHouseService.findById(device.getStorehouseId());
        if (site != null) {
            dispatchHistory.setOldsiteId(site.getInstallSiteId());
            dispatchHistory.setOldsiteName(site.getName());
        }
        setDispatchValue(device, dispatchHistory, storehouse);
        dispatchHistory.setType(CommonConstant.BACK_OFF);
        dispacheHistoryService.save(dispatchHistory);
    }

    /**
     * 设置dispatchLog值
     */
    private void setDispatchValue(TdeviceInventory device, TdeviceDispatchHistory dispatchHistory, TdeviceStoreHouse storehouse) {
        dispatchHistory.setStoreHouse(storehouse);
//        dispatchHistory.setDeviceNew(JSONObject.toJSONString(device));
        dispatchHistory.setImei(device.getImei());
        dispatchHistory.setSnNo(device.getSnNo());
        dispatchHistory.setOperatorId(device.getOperatorId());
        dispatchHistory.setOperatorName(device.getOperatorName());
        dispatchHistory.setIsp(device.getIsp());
        dispatchHistory.setBoxNumber(device.getBoxNumber());
        dispatchHistory.setRegion(regionService.findById(device.getRegionId()));
        dispatchHistory.setManufactor(manufactorService.findById(device.getManufactorId()));
        dispatchHistory.setIotType(iotTypeService.findById(device.getIotTypeId()));

    }
    /**
     * 验证退库设备信息
     */
    private void validBackoffInfo(TdeviceInventory device) {
        if (device == null) {
            log.error("device is not exist.");
            throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
        }
        if (device.getInstallSiteId() == null) {
            log.error("device's siteId is null.imei:" + device.getImei());
            throw new NbiotException(DeviceExceptionEnum.E_00012.getCode(), DeviceExceptionEnum.E_00012.getMessage());
        }
        if (device.getInstallSiteId() != null && Objects.equals(1, device.getStorehouseId())) {
            log.error("device's storehouse is Hua Qiang.imei:" + device.getImei());
            throw new NbiotException(DeviceExceptionEnum.E_00012.getCode(), DeviceExceptionEnum.E_00012.getMessage());
        }
        if (device.getDevstate() != null && Objects.equals(1, device.getDevstate())) {
            log.error("device has installed.imei:{}, devstate:{}", device.getImei(), device.getDevstate());
            throw new NbiotException(DeviceExceptionEnum.E_00032.getCode(), DeviceExceptionEnum.E_00032.getMessage());
        }
    }

    /**
     *构造不良品数据
     */
    private TdeviceDefectiveInventory structorDefectiveData(TdeviceInventory device) {
        TdeviceDefectiveInventory defectiveInventory = new TdeviceDefectiveInventory();
        defectiveInventory.setBoxNumber(device.getBoxNumber());
        defectiveInventory.setDevDesc(device.getDevDesc());
        defectiveInventory.setDevname(device.getDevname());
        defectiveInventory.setDevstate(device.getDevstate());
        defectiveInventory.setDevtype(device.getDevtype());
        defectiveInventory.setIccid(device.getIccid());
        defectiveInventory.setImei(device.getImei());
        defectiveInventory.setImsi(device.getImsi());
        defectiveInventory.setInstallSite(null);
        defectiveInventory.setIotDeviceId(device.getIotDeviceId());
        defectiveInventory.setOperatorId(device.getOperatorId());
        defectiveInventory.setOperatorName(device.getOperatorName());
        defectiveInventory.setSnNo(device.getSnNo());
        defectiveInventory.setSwVersion(device.getSwVersion());
        defectiveInventory.setModelNo(device.getModelNo());
        defectiveInventory.setIotType(iotTypeService.findById(device.getIotTypeId()));
        defectiveInventory.setManufactor(manufactorService.findById(device.getManufactorId()));
        defectiveInventory.setRegion(regionService.findById(device.getRegionId()));
        defectiveInventory.setStorehouse(storeHouseService.findById(device.getStorehouseId()));
        return defectiveInventory;
    }

    private List<TdeviceInventory> findByBoxNumbers(List<String> boxNumbers) {
        return deviceInventoryRepository.findByBoxNumberIn(boxNumbers);
    }

    private List<TdeviceInventory> findByBoxNumber(String boxNumber) {
        return deviceInventoryRepository.findByBoxNumber(boxNumber);
    }

    /**
     * 删除设备相关缓存
     */
    @Override
    public Integer deleteDeviceRedisData(){
        int count = 0;
        if (redisUtil.keys(CommonConstant.DEVICE_PATTERN) != null && redisUtil.keys(CommonConstant.DEVICE_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.DEVICE_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
                count ++;
            }
        }
       int siteCount = installSiteService.deleteInstallSiteRedisData();
       int dispatchCount = dispacheHistoryService.deleteDisPatchRedis();
        log.info("delete dispatch log redis count is:{}, site count:{}", dispatchCount, siteCount);
        return count + dispatchCount + siteCount;
    }

    @Override
    public Map<String, Integer> findOperatorIdAndImeiRelations() {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Object>> lists = deviceInventoryRepository.findOperatorIdAndImeiRelations();
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                String imei = (String) map.get("imei");
                Integer operatorId = (Integer) map.get("operator_id");
                result.put(imei, operatorId);
            }
        }
        return result;
    }

    @Override
    public Map<String, List<DeviceOfBox>> getDispatchBoxNumbers(DeviceQuery query) {
        List<DeviceOfBox> CTcounts = new ArrayList<>();
        List<DeviceOfBox> CMCCcounts = new ArrayList<>();
        List<DeviceOfBox> CUCCcounts = new ArrayList<>();
        List<Integer> operatorIds = new ArrayList<>();
        List<Map<String, Object>> lists = null;
        if (query != null && query.getOperatorIdList() != null) {
            operatorIds = query.getOperatorIdList();
            lists = deviceInventoryRepository.getDispatchBoxNumbers(operatorIds);
        } else {
            lists = deviceInventoryRepository.getDispatchBoxNumbers();
        }
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                DeviceOfBox deviceOfBox = new DeviceOfBox();
                Long totalCount = Long.valueOf(map.get("totalCount").toString());
                String boxNumber = (String) map.get("box_number");
                String isp = (String) map.get("isp");
                String storehouseName = (String) map.get("storehouse_name");
                //如果有值，说明该箱子下有设备已分配到安装点，不能显示
                List<Integer> siteIds = deviceInventoryRepository.countInstallSiteIdByBoxNumber(boxNumber);
                if(siteIds !=null && siteIds.size() > 0){
                    log.info("boxnumber {} has devices dispatch to site.", boxNumber);
                    continue;
                }
                deviceOfBox.setIsp(isp);
                deviceOfBox.setBoxDeviceNumber(totalCount);
                deviceOfBox.setBoxNumber(boxNumber);
                deviceOfBox.setStoreHouseName(storehouseName);
                setISPValue(CTcounts, CMCCcounts, CUCCcounts, deviceOfBox, isp);
            }
        }
        Map<String, List<DeviceOfBox>> map = new HashMap<>();
        map.put(CommonConstant.CT, CTcounts);
        map.put(CommonConstant.CMCC, CMCCcounts);
        map.put(CommonConstant.CUCC, CUCCcounts);
        return map;
    }

    @Override
    public Integer updateStoreNameByStorehouseId(String storeName, Integer id) {
        return deviceInventoryRepository.updateStoreNameByStorehouseId(storeName, id);
    }

    @Override
    public Integer updateSiteNameBySiteId(String name, Integer installSiteId) {
        return deviceInventoryRepository.updateSiteNameBySiteId(name, installSiteId);
    }

    @Override
    public Integer removeGroupByImeis(List<String> imeis) {
        return deviceInventoryRepository.removeGroupByImeis(imeis);
    }

    @Override
    public Integer countStoreCountByOperatorIds(List<Integer> operatorIds) {
        return deviceInventoryRepository.countStoreCountByOperatorIds(operatorIds);
    }

    @Override
    public Integer countAllStoreCountByOperatorIds(List<Integer> operatorIds) {
        List<String> imeis = saleRecordService.findImeisByOperatorIds(operatorIds);
        if(imeis != null && !imeis.isEmpty()){
            return deviceInventoryRepository.countAllStoreCountByOperatorIds(operatorIds, imeis);
        }
        return deviceInventoryRepository.countAllStoreCountByOperatorIds(operatorIds);
    }

    @Override
    public Integer countInstalledCountByOperatorIds(List<Integer> operatorIds) {
        return deviceInventoryRepository.countInstalledCountByOperatorIds(operatorIds);
    }

    @Override
    public Integer countSellCountByOperatorIds(List<Integer> operatorIds) {
        List<String> imeis = activeInfoService.findImeisByOperatorIds(operatorIds);
        if(imeis != null && !imeis.isEmpty()){
            return deviceInventoryRepository.countSellCountByOperatorIds(operatorIds, imeis);
        }
        return deviceInventoryRepository.countSellCountByOperatorIds(operatorIds);
    }

    @Override
    public List<String> getUnSellDevicesByStorehouseId(Integer id) {
        return deviceInventoryRepository.getUnSellDevicesByStorehouseId(id);
    }

    @Override
    public Page<TdeviceInventory> getBoxNumbersOfPutStorage(Integer page, Integer size, DeviceQuery deviceQuery) {
        Sort sort = SortUtil.sort("id", "desc");
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("getBoxNumbersOfPutStorage:" + JSONObject.toJSONString(deviceQuery));
        if(deviceQuery.getOperatorIdList() == null){
            if(deviceQuery.getIsPutStorage() == null){
                log.info("getIsPutStorage is null.");
                return deviceInventoryRepository.getBoxNumbersOfPutStorage(deviceQuery.getBoxNumber(), deviceQuery.getOperatorName(), pageable);
            }
            log.info("getIsPutStorage is not null." + deviceQuery.getIsPutStorage());
            return deviceInventoryRepository.getBoxNumbersOfPutStorage(deviceQuery.getBoxNumber(), deviceQuery.getOperatorName(), deviceQuery.getIsPutStorage(), pageable);
        }
        else{
            //显示全部
            if(deviceQuery.getIsPutStorage() == null){
                return deviceInventoryRepository.getBoxNumbersOfPutStorage(deviceQuery.getBoxNumber(), deviceQuery.getOperatorName(), deviceQuery.getOperatorIdList(),  pageable);
            }
            return deviceInventoryRepository.getBoxNumbersOfPutStorage(deviceQuery.getBoxNumber(), deviceQuery.getStorehouseName(), deviceQuery.getOperatorName(), deviceQuery.getOperatorIdList(), deviceQuery.getIsPutStorage(), pageable);
        }
    }

    @Override
    public List<String> getImeisByGroupId(Integer groupId) {
        return deviceInventoryRepository.getImeisByGroupId(groupId);
    }

    @Override
    public List<TdeviceInventory> getDevicesByBoxNumber(String boxNumber) {
        return findByBoxNumber(boxNumber);
    }

    @Override
    public RegisterVo getInstalledWorkerByImei(String imei) {
        List<Map<String, Object>> lists = deviceInventoryRepository.getInstalledWorkerByImei(imei);
        if(lists != null && !lists.isEmpty()){
            RegisterVo vo = new RegisterVo();
            for(Map<String, Object> map : lists){
                String workerName = (String)map.get("name");
                Date createTime = (Date)map.get("create_time");
                vo.setDate(createTime);
                vo.setInstallWorkerName(workerName);
            }
            return vo;
        }
        return null;
    }

    @Override
    public Map<String, RegisterVo> getInstalledWorkerByImeis(List<String> imeis) {
        Map<String, RegisterVo> result = new HashMap<>();
        List<Map<String, Object>> lists = deviceInventoryRepository.getInstalledWorkerByImeis(imeis);
        if(lists != null && !lists.isEmpty()){
            RegisterVo vo = new RegisterVo();
            for(Map<String, Object> map : lists){
                String workerName = (String)map.get("name");
                Date createTime = (Date)map.get("create_time");
                String imei = (String)map.get("imei");
                vo.setDate(createTime);
                vo.setInstallWorkerName(workerName);
                result.put(imei, vo);
            }
        }
        return result;
    }

    @Override
    public List<TdeviceInventory> getDeliverSns(List<Integer> operatorIds) {
        List<TdeviceInventory> deviceList = new ArrayList<>();
        log.info("operatorIds:" + JSONObject.toJSONString(operatorIds));
        if(operatorIds == null || Objects.equals(0, operatorIds.get(0))){
           List<Map<String, Object>> lists = deviceInventoryRepository.getDeliverSns();
            if (setDeliverSnValue(deviceList, lists)) return deviceList;
        }
        List<Map<String, Object>> lists = deviceInventoryRepository.getDeliverSns(operatorIds);
        if (setDeliverSnValue(deviceList, lists)) return deviceList;
        return deviceList;
    }

    @Override
    public List<TdeviceInventory> getInfoByImeis(List<String> imeis) {
        return deviceInventoryRepository.findByImeiIn(imeis);
    }

    @Override
    public List<TdeviceInventory> getAllInstalledDevices() {
        return deviceInventoryRepository.getAllInstalledDevices();
    }

    private boolean setDeliverSnValue(List<TdeviceInventory> deviceList, List<Map<String, Object>> lists) {
        if (lists != null && !lists.isEmpty()) {
            for (Map<String, Object> map : lists) {
                String deliverSn = String.valueOf(map.get("ddeliver_sn"));
                if("null".equals(deliverSn)){
                    continue;
                }
                Date createTime = null;
                if (map.get("create_time") != null) {
                    createTime = (Date) map.get("create_time");
                }
                String operatorName = String.valueOf(map.get("operator_name"));
                String isp = String.valueOf(map.get("isp"));
                String storehouseName = String.valueOf(map.get("storehouse_name"));
                Integer devstate = Integer.valueOf(String.valueOf(map.get("devstate")));
                TdeviceInventory device = new TdeviceInventory();
                device.setOperatorName(operatorName);
                device.setDeliverSn(deliverSn);
                device.setStorehouseName(storehouseName);
                device.setCreateTime(createTime);
                device.setIsp(isp);
                device.setDevstate(devstate);
                List<String> boxNumbers = null;
                if(StringUtils.isNotBlank(deliverSn)){
                    boxNumbers = deviceInventoryRepository.findBoxNumbersByDeliverSns(Arrays.asList(deliverSn));
                }
                device.setBoxNumbers(boxNumbers);
                deviceList.add(device);
            }
            return true;
        }
        return false;
    }

    private void broadcastTrdPlatform(List<String> boxNumbers) {
        // 通知第三方平台入库
        JSONObject json = new JSONObject();
        String jsonString = boxNumbers.stream().collect(Collectors.joining(","));
        json.put("container_sn", jsonString);
        iemp.nui.common.model.Result result = containerService.deliverDone(json);
        log.info("3rd deliverDone json parameter:" + json + ",result:" + result);
        if (Objects.equals(result.getCode(), 0)){
            log.error("3rd deliverDone error.result:" + result);
            throw new NbiotException(400025, "通知第三方平台入库失败");
        }
    }

}
