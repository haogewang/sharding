package com.szhq.iemp.device.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.enums.exception.StorehouseExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceStoreHouse;
import com.szhq.iemp.device.api.model.TpolicyInfo;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.enums.PolicyNameEnum;
import com.szhq.iemp.device.api.vo.query.StorehouseQuery;
import com.szhq.iemp.device.repository.DeviceStoreHouseRepository;
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
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DeviceStoreHouseServiceImpl implements DeviceStoreHouseService {

    @Resource
    private DeviceStoreHouseRepository storeHouseRepository;

    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private ElectrmobileService electrmobileService;
    @Autowired
    private PolicyInfoService policyInfoService;

    @Override
    public TdeviceStoreHouse add(TdeviceStoreHouse entity) {
        if(entity.getOperatorId() == null){
            log.error("storehouse operatorId can not be null.");
            throw new NbiotException(400, "");
        }
        if(entity.getParent() != null && entity.getParent().getId() != null && !Objects.equals(0, entity.getParent().getId())){
            Integer parentStoreId = entity.getParent().getId();
            if(Objects.equals(parentStoreId, entity.getId())) {
                throw new NbiotException(StorehouseExceptionEnum.E_0009.getCode(), StorehouseExceptionEnum.E_0009.getMessage());
            }
            if(StringUtils.isEmpty(entity.getName())){
                throw new NbiotException(400, StorehouseExceptionEnum.E_00010.getMessage());
            }
            //名称是否重复只对新增做校验(编辑单独校验)
            if (findByName(entity.getName()) != null) {
                log.error("storehouse name already exist!storehouse Name:" + entity.getName());
                throw new NbiotException(StorehouseExceptionEnum.E_00011.getCode(), StorehouseExceptionEnum.E_00011.getMessage());
            }
            TdeviceStoreHouse parentStore = findById(parentStoreId);
            if(parentStore == null){
                log.error("parent store house is not exist.parentId:" + parentStoreId);
                throw new NbiotException(404, StorehouseExceptionEnum.E_0003.getMessage());
            }
//            Integer operatorId = operatorService.findOperatorIdByStoreHouseId(parentStoreId);
            entity.setOperatorId(parentStore.getOperatorId());
            entity.setIsActive(parentStore.getIsActive());
            entity.setPolicyNameCode(parentStore.getPolicyNameCode());
            entity.setStoreLevel(parentStore.getStoreLevel() + 1);
        }

        return save(entity);
    }

    @Override
    public TdeviceStoreHouse update(TdeviceStoreHouse entity) {
        if(entity.getId() == null){
            log.error("store house id is null");
            throw new NbiotException(404, StorehouseExceptionEnum.E_0006.getMessage());
        }
        TdeviceStoreHouse storeHouse = findById(entity.getId());
        if(storeHouse == null){
            log.error("store house is not exist.id:" + entity.getId());
            throw new NbiotException(404, StorehouseExceptionEnum.E_00012.getMessage());
        }
        if(!storeHouse.getName().equals(entity.getName())){
            if(findByName(entity.getName()) != null){
                log.error("store name already exist! name:" + entity.getName());
                throw new NbiotException(StorehouseExceptionEnum.E_00011.getCode(), StorehouseExceptionEnum.E_00011.getMessage());
            }
        }
        if(storeHouse.getParent() != null && Objects.equals(storeHouse.getParent().getId(), entity.getId())) {
            throw new NbiotException(StorehouseExceptionEnum.E_0009.getCode(), StorehouseExceptionEnum.E_0009.getMessage());
        }
        List<String> imeis = deviceInventoryService.getUnSellDevicesByStorehouseId(entity.getId());
        if(!Objects.equals(storeHouse.getIsActive(), entity.getIsActive())){
            if(imeis != null && !imeis.isEmpty()){
                log.info("update storehouse active attribute.storehouseId:{},isActive:{}", entity.getId(), entity.getIsActive());
                deviceInventoryService.updateActiveStateByImeis(imeis, entity.getIsActive());
            }
        }
        if(entity.getPolicyNameCode() != null && !Objects.equals(storeHouse.getPolicyNameCode(), entity.getPolicyNameCode())){
            if(imeis != null && !imeis.isEmpty()){
                log.info("update storehouse policy.storehouseId:{},policyCode:{},deviceCount:{}", entity.getId(), entity.getPolicyNameCode(), imeis.size());
                for(String imei : imeis){
                    TpolicyInfo policyInfo = new TpolicyInfo();
                    policyInfo.setImei(imei);
                    policyInfo.setNameCode(entity.getPolicyNameCode());
                    policyInfo.setName(PolicyNameEnum.getNameByCode(entity.getPolicyNameCode()));
                    policyInfoService.updatePolicy(policyInfo);
                }
            }
        }
        else if(entity.getPolicyNameCode() == null){
            if(imeis != null && !imeis.isEmpty()){
                log.info("delete storehouse policy.storehouseId:{},deviceCount:{}", entity.getId(), imeis.size());
                Integer count = policyInfoService.deleteNoInstalledPolicyByImeis(imeis);
                log.info("delete noInstalledPolicy count:" + count);
            }
            storeHouse.setPolicyNameCode(null);
        }
        BeanUtils.copyProperties(entity, storeHouse, PropertyUtil.getNullProperties(entity));
        if(StringUtils.isNotEmpty(entity.getName()) && !Objects.equals(entity.getName(), storeHouse.getName())){
           Integer i = deviceInventoryService.updateStoreNameByStorehouseId(entity.getName(), entity.getId());
           Integer j = electrmobileService.updateStoreNameByStorehouseId(entity.getName(), entity.getId());
           log.info("update storehouse name count.device:{},elec:{}",i, j);
           deviceInventoryService.deleteDeviceRedisData();
           electrmobileService.deleteElecRedisData();
        }
        return save(storeHouse);
    }

    @Override
    public TdeviceStoreHouse save(TdeviceStoreHouse entity) {
        return storeHouseRepository.save(entity);
    }

    @Override
    public TdeviceStoreHouse findByName(String name) {
        return storeHouseRepository.findByName(name);
    }

    @Override
    public TdeviceStoreHouse findById(Integer id) {
        return storeHouseRepository.findById(id).orElse(null);
    }

    @Override
    public List<TdeviceStoreHouse> findByParentId(Integer pId) {
        return storeHouseRepository.findByParentId(pId);
    }

    @Override
    public List<Integer> findStorIdsByOperatorId(Integer operatorId) {
        return storeHouseRepository.findStorIdsByOperatorId(operatorId);
    }

    @Override
    public Integer deleteById(Integer id) {
        Integer i = 0;
        Long count = deviceInventoryService.countByStoreHouseId(id);
        if(count > 0){
            log.error("the storehouse has devices.can not delete.store id:" + id);
            throw new NbiotException(500, StorehouseExceptionEnum.E_0004.getMessage());
        }
        List<TdeviceStoreHouse> subStoreHouses = findAllChildIds(id);
        if(subStoreHouses != null && !subStoreHouses.isEmpty()){
            List<Integer> subStoreIds = subStoreHouses.stream().map(TdeviceStoreHouse::getId).collect(Collectors.toList());
            Long subCount = deviceInventoryService.countByStoreHouseIds(subStoreIds);
            if(subCount > 0){
                log.error("sub device has devices.can not delete.store subIds:" + JSONObject.toJSONString(subStoreIds));
                throw new NbiotException(500, StorehouseExceptionEnum.E_0005.getMessage());
            }
            i = storeHouseRepository.deleteByStoreIds(subStoreIds);
        }

        return i;
    }

    @Override
    public Integer deleteByIds(List<Integer> ids) {
        int i = storeHouseRepository.deleteByStoreIds(ids);
        return i;
    }

    @Override
    public Integer deleteByOperatorIds(List<Integer> operatorIds) {
        int i = storeHouseRepository.deleteByOperatorIds(operatorIds);
        return i;
    }

    @Override
    public List<ActiveDeviceCount> deviceActiveStatistic(Integer id, StorehouseQuery query) {
        List<ActiveDeviceCount> result = new ArrayList<>();
        List<Map<String, Object>> lists = null;
        if(id == null && query != null && query.getOperatorIdList() != null && query.getOperatorIdList().get(0) != 0){
            lists = storeHouseRepository.deviceActiveStatistic(query.getOperatorIdList());
        }
        else if(id != null && query != null && query.getOperatorIdList() != null){
            lists = storeHouseRepository.deviceActiveStatistic(id, query.getOperatorIdList());
        }
        else {
            lists = storeHouseRepository.deviceActiveStatistic();
        }
        if(lists != null && !lists.isEmpty()){
            for (Map<String, Object> map : lists) {
                long total  = Long.valueOf(String.valueOf(map.get("total")));
                long activeCount = Long.valueOf(String.valueOf(map.get("active_count")));
                long noActiveCount = Long.valueOf(String.valueOf(map.get("no_active_count")));
                int storehouseId  = Integer.valueOf(String.valueOf(map.get("storehouse_id")));
                String storehouseName  = String.valueOf(map.get("storehouse_name"));
                String operatorName  = String.valueOf(map.get("operator_name"));
                ActiveDeviceCount entity = new ActiveDeviceCount();
                entity.setActiveCount(activeCount);
                entity.setNoActiveCount(noActiveCount);
                entity.setStorehouseId(storehouseId);
                entity.setStorehouseName(storehouseName);
                entity.setOperatorName(operatorName);
                entity.setTotalCount(total);
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public List<TdeviceStoreHouse> findAllStoresByOperatorId(Integer operatorId) {
        return storeHouseRepository.findAllStoresByOperatorId(operatorId);
    }

    @Override
    public MyPage<TdeviceStoreHouse> findAllByCriteria(Integer page, Integer size, String sorts, String orders, StorehouseQuery myQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TdeviceStoreHouse> pages = storeHouseRepository.findAll(new Specification<TdeviceStoreHouse>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TdeviceStoreHouse> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (myQuery != null) {
                    if (myQuery.getStorehouseId() != null) {
                        list.add(criteriaBuilder.equal(root.get("id").as(Integer.class), myQuery.getStorehouseId()));
                    }
                    if (myQuery.getOperatorId() != null) {
                        list.add(criteriaBuilder.equal(root.get("operatorId").as(Integer.class), myQuery.getOperatorId()));
                    }
//                    if (myQuery.getOperatorIdList() != null) {
//                        list.add(root.get("operatorId").as(Integer.class).in(myQuery.getOperatorIdList()));
//                    }
                    if (StringUtils.isNotEmpty(myQuery.getStorehouseName())) {
                        list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + myQuery.getStorehouseName() + "%"));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TdeviceStoreHouse>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    /**
     * 找到所有子类
     */
    public List<TdeviceStoreHouse> findAllChildIds(Integer parentId) {
        List<TdeviceStoreHouse> storeHouseList = new ArrayList<>();
        findById(parentId, storeHouseList);
        storeHouseList.add(findById(parentId));
        return storeHouseList;
    }

    private void findById(Integer parentId, List<TdeviceStoreHouse> storeHouseList) {
        List<TdeviceStoreHouse> lists = findByParentId(parentId);
        if(lists != null && lists.size() > 0) {
            for(TdeviceStoreHouse storeHouse : lists) {
                storeHouseList.add(storeHouse);
                findById(storeHouse.getId(), storeHouseList);
            }
        }
    }


}
