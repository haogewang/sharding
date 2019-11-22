package com.szhq.iemp.device.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.RegionExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.StorehouseExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.ListTranscoder;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.*;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.api.vo.query.OperatorQuery;
import com.szhq.iemp.device.repository.OperatorRepository;
import com.szhq.iemp.device.util.RedisUtil;
import iemp.nui.api.ContainerService;
import iemp.nui.common.model.ChangeOperator;
import iemp.nui.common.model.OperatorModel;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @author wanghao
 * @date 2019/10/18
 */
@Slf4j
@Component
@Service(interfaceClass = OperatorService.class,timeout = 30000, retries = 0)
@Transactional
@CacheConfig(cacheNames = "aepOperator")
public class OperatorServiceImpl implements OperatorService {

    @Reference
    private ContainerService containerService;

    @Resource
    private OperatorRepository operatorRepository;
    @Autowired
    private DeviceStoreHouseService deviceStoreHouseService;
    @Autowired
    private AddressRegionService adressRegionService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private RedisUtil redisUtil;

    @Cacheable(unless="#result == null || #result.getTotal() == 0")
    @Override
    public MyPage<Toperator> findAllByCriteria(Integer page, Integer size, String sorts, String orders, OperatorQuery myQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Toperator> pages = operatorRepository.findAll(new Specification<Toperator>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Toperator> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (myQuery != null) {
                    if (StringUtils.isNotEmpty(myQuery.getOperatorName())) {
                        list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + myQuery.getOperatorName() + "%"));
                    }
                    if (myQuery.getParentId() != null) {
                        list.add(criteriaBuilder.equal(root.get("parent").get("id").as(Integer.class), myQuery.getParentId()));
                    }
                    if (myQuery.getOperatorIdList() != null) {
                        list.add(root.get("id").as(Integer.class).in(myQuery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<Toperator>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public List<Toperator> findAllChildrenInfo(Integer parentId) {
        if(Objects.equals(0, parentId)){
            return findAll();
        }
        return findAllChildren(parentId);
    }

    @Override
    public List<Toperator> findAll() {
        return operatorRepository.findAll();
    }

    @Override
    public Integer findOperatorIdByStoreHouseId(Integer storehouseId) {
        Integer operatorId = operatorRepository.findOperatorIdByStoreHouseId(storehouseId);
        return operatorId;
    }

    @Override
    public Toperator add(Toperator entity) {
        TaddressRegion addressRegion = validParameter(entity);
        TdeviceStoreHouse deviceStoreHouse = new TdeviceStoreHouse();
        deviceStoreHouse.setAddress(entity.getAddress());
        deviceStoreHouse.setName(entity.getName() + "仓库");
        if(entity.getParent() == null) {
            deviceStoreHouse.setParent(null);
        }
        else {
            Toperator parentOperator = findById(entity.getParent().getId());
            if(parentOperator != null) {
                if(Objects.equals(parentOperator.getId(), entity.getId())) {
                    throw new NbiotException(600005, OperatorExceptionEnum.E_0006.getMessage());
                }
                deleteRedisData(parentOperator.getId());
            }else {
                log.error("parent operator is not exist." + JSONObject.toJSONString(entity));
                throw new NbiotException(600002, OperatorExceptionEnum.E_0003.getMessage());
            }
        }
        deviceStoreHouse.setLat(entity.getLat());
        deviceStoreHouse.setLon(entity.getLon());
        deviceStoreHouse.setRegion(addressRegion);
        TdeviceStoreHouse tdeviceStoreHouse = deviceStoreHouseService.save(deviceStoreHouse);
        if(tdeviceStoreHouse == null) {
            log.error("create device stores failed." + JSONObject.toJSONString(deviceStoreHouse));
            throw new NbiotException(StorehouseExceptionEnum.E_0008.getCode(), StorehouseExceptionEnum.E_0008.getMessage());
        }
        entity.setStorehouseId(tdeviceStoreHouse.getId());
        entity.setRegion(addressRegion);
        Toperator operatingCompany = save(entity);
        tdeviceStoreHouse.setOperatorId(operatingCompany.getId());
        deviceStoreHouseService.add(tdeviceStoreHouse);
        log.info("begin broadcast to 3rd platform to add operator...");
        broadcastToThirdPlatformAdd(addressRegion, operatingCompany);
        log.info("save operator success, id is:" + operatingCompany.getId());
        deleteRedisData(operatingCompany.getId());
        deleteRedisOperator();
        return operatingCompany;
    }

    @Override
    public Toperator save(Toperator entity) {
        return operatorRepository.save(entity);
    }

    @Override
    public Toperator update(Toperator entity) {
        if(entity.getId() == null) {
            throw new NbiotException(400, "");
        }
        TaddressRegion addressRegion = validParameter(entity);
        Toperator operator = findById(entity.getId());
        if(operator != null && !operator.getName().equals(entity.getName())){
            if(findByName(entity.getName()) != null){
                log.error("operator name already exist! name:" + entity.getName());
                throw new NbiotException(600004, OperatorExceptionEnum.E_0005.getMessage());
            }
        }
        if(operator.getParent() != null && Objects.equals(operator.getParent().getId(), entity.getId())) {
            throw new NbiotException(600005, OperatorExceptionEnum.E_0006.getMessage());
        }
        BeanUtils.copyProperties(entity, operator, PropertyUtil.getNullProperties(entity));
        Toperator toperator = save(operator);
        log.info("begin broadcast to 3rd platform to update operator...");
        broadcastToThirdPlatformUpdate(addressRegion, toperator);
        if(entity.getParent() != null && operator.getParent() != null && !Objects.equals(entity.getParent().getId(), operator.getParent().getId())){
            deleteRedisData(entity.getParent().getId());
        }
        deleteRedisData(entity.getId());
        deleteRedisOperator();
        return toperator;
    }

    @Override
    public Integer delete(Integer id) {
        int i = 0;
        Toperator operator = findById(id);
        if(operator != null) {
            List<Integer> ids = findAllChildIds(id);
            Long count = deviceInventoryService.countByOperatorIds(ids);
            if(count > 0){
                log.error("can not delete operator, the operator has devices.");
                throw new NbiotException(600009, OperatorExceptionEnum.E_00010.getMessage());
            }
            List<Integer> storeIds = operatorRepository.findStorehouseIdByOperatorIds(ids);
            if(storeIds != null && !storeIds.isEmpty()){
                int j = deviceStoreHouseService.deleteByIds(storeIds);
                log.info("delete storehouse count:" + j);
            }
            int j = operatorRepository.deleteByOperatIds(ids);
            log.info("delete operator id:{},count:{}", JSONObject.toJSONString(ids), j);
            i = j;
        } else {
            throw new NbiotException(600001, OperatorExceptionEnum.E_0002.getMessage());
        }
        log.info("begin broadcast to 3rd platform to delete operator...");
        broadcastToThirdPlatformDelete(id);
        deleteRedisData(operator.getId());
        deleteRedisOperator();
        return i;
    }

    @Override
    @Cacheable(unless="#result == null")
    public Toperator findByName(String name) {
        return operatorRepository.findByName(name);
    }

    @Override
    @Cacheable(unless="#result == null")
    public Toperator findById(Integer id) {
        return operatorRepository.findById(id).orElse(null);
    }


    //提供dubbo服务，慎改！
    @Override
    public List<Integer> findAllChildIds(Integer parentId) {
        Object datas = redisUtil.get(CommonConstant.OPERATOR_REDISKEY + parentId);
        ListTranscoder<Integer> listTranscoder = new ListTranscoder<Integer>();
        if(datas == null) {
            List<Integer> ids = new ArrayList<>();
            findById(parentId, ids);
            ids.add(parentId);
            if(ids.size() > 0) {
                log.debug("");
                redisUtil.set(CommonConstant.OPERATOR_REDISKEY + parentId, listTranscoder.serialize(ids));
            }
            return ids;
        }
        Object o = listTranscoder.deserialize((String)datas);
        log.info("get operator ids data from redis. operator id:" + parentId);
        return (List<Integer>)o;
    }

    private void findById(Integer parentId, List<Integer> ids) {
        List<Map<String, Object>>  lists = operatorRepository.findAllChildrenById(parentId);
        if(lists != null && lists.size() > 0) {
            for(Map<String, Object> map : lists) {
                long id = Long.valueOf(String.valueOf(map.get("id")));
                int oId = Integer.valueOf(String.valueOf(id));
                ids.add(oId);
                findById(oId, ids);
            }
        }
    }

    @Override
    public Integer deleteRedisData(Integer parentId) {
        Toperator toperator = findById(parentId);
        if(toperator != null && toperator.getParent() != null){
            redisUtil.del(CommonConstant.OPERATOR_REDISKEY + parentId);
            deleteRedisData(toperator.getParent().getId());
        }else{
            redisUtil.del(CommonConstant.OPERATOR_REDISKEY + parentId);
        }
        return 1;
    }

    @Override
    public List<Toperator> findByParent(Integer id) {
        List<Toperator> result = new ArrayList<>();
        if(id == 0){
            result = operatorRepository.getAllFirstOperator();
        }else{
            result = operatorRepository.getChildrenByPId(id);
        }
        return result;
    }

    @Override
    public List<Toperator> findNeedPutStorageOperators() {
        List<Integer> operatorIds = operatorRepository.findNeedPutStorage();
        if(operatorIds != null && !operatorIds.isEmpty()){
            return findByIds(operatorIds);
        }
        return null;
    }

    @Override
    public List<Toperator> findByParentIdAndStoreIsActive(Integer id, Boolean isStoreActive) {
        List<Toperator> result = new ArrayList<>();
        if(id == 0){
            result = operatorRepository.getAllFirstTrueOperator(isStoreActive);
        }else{
            result = operatorRepository.getChildrenByPIdAndType(id, isStoreActive);
        }
        return result;
    }

    private List<Toperator> findByIds(List<Integer> operatorIds) {
        return operatorRepository.findByIds(operatorIds);
    }

    private void deleteRedisOperator() {
        Set<String> sets = redisUtil.keys(CommonConstant.OPERATOR_PATTERN);
        if(sets != null && sets.size() > 0){
            for(String key : sets) {
                log.info("del redis key:" + key);
                redisUtil.del(key);
            }
        }
    }

    private List<Toperator> findAllChildren(Integer parentId) {
        List<Toperator> operators = new ArrayList<>();
        findOperatorById(parentId, operators);
        operators.add(findById(parentId));
        return operators;
    }
//
    private void findOperatorById(Integer parentId, List<Toperator> ids) {
        List<Toperator>  lists = operatorRepository.findNextOperator(parentId);
        if(lists != null && lists.size() > 0) {
            for(Toperator operator : lists) {
                ids.add(operator);
                findOperatorById(operator.getId(), ids);
            }
        }
    }

    /**
     * 参数校验
     */
    private TaddressRegion validParameter(Toperator entity) {
        if(StringUtils.isEmpty(entity.getName())){
            throw new NbiotException(600003, OperatorExceptionEnum.E_0004.getMessage());
        }
        //名称是否重复只对新增做校验(编辑单独校验)
        if (findByName(entity.getName()) != null && entity.getId() == null) {
            log.error("operator name already exist!operatorName:" + entity.getName());
            throw new NbiotException(600004, OperatorExceptionEnum.E_0005.getMessage());
        }
        if (entity.getRegion() == null || entity.getRegion().getId() == null) {
            log.error("operator region is null." + JSONObject.toJSONString(entity));
            throw new NbiotException(RegionExceptionEnum.E_0004.getCode(), RegionExceptionEnum.E_0004.getMessage());
        }
        TaddressRegion addressRegion = adressRegionService.findById(entity.getRegion().getId());
        if (addressRegion == null) {
            log.error("region is not exist. region id:" + entity.getRegion().getId());
            throw new NbiotException(RegionExceptionEnum.E_0003.getCode(), RegionExceptionEnum.E_0003.getMessage());
        }
        return addressRegion;
    }

    /**
     * 通知第三方发货平台添加运营公司
     * @param addressRegion 区域
     * @param operator 运营公司
     */
    private void broadcastToThirdPlatformAdd(TaddressRegion addressRegion, Toperator operator) {
        TcommonConfig commonConfig = commonService.findByName(CommonConstant.CURRENT_ENV_KEY);
        if (commonConfig == null){
            throw new NbiotException(500, "no current-env config find! please config it.");
        }
        OperatorModel operatorModel = new OperatorModel();
        operatorModel.setAddress(addressRegion.getAreaName());
        operatorModel.setDevname("tracker");
        operatorModel.setLat(operator.getLat());
        operatorModel.setLon(operator.getLon());
        operatorModel.setAddress_region_id(String.valueOf(addressRegion.getId()));
        operatorModel.setDeliver_com_name(commonConfig.getValue() + "-" + operator.getName());
        operatorModel.setOperator_com_name(operator.getName());
        operatorModel.setOperator_id(String.valueOf(operator.getId()));
        if(operator.getParent() != null){
            operatorModel.setOperator_parent_id(String.valueOf(operator.getParent().getId()));
        }
        operatorModel.setPlatform(commonConfig.getValue());
        log.info("operatorModel:" + JSONObject.toJSONString(operatorModel));
        Result result = containerService.createOperator(operatorModel);
        log.info("operator result:" + result);
        if(Objects.equals(result.getCode(), 0)){
            log.error("operator result:" + result);
            throw new NbiotException(600006, OperatorExceptionEnum.E_0007.getMessage());
        }
    }

    /**
     * 通知第三方发货平台修改运营公司
     */
    private void broadcastToThirdPlatformUpdate(TaddressRegion adressRegion, Toperator operator) {
        TcommonConfig commonConfig = commonService.findByName(CommonConstant.CURRENT_ENV_KEY);
        if (commonConfig == null){
            throw new NbiotException(500, "no current-env config find! please config it.");
        }
        ChangeOperator changeOperator = new ChangeOperator();
        changeOperator.setAddress(adressRegion.getAreaName());
        changeOperator.setAddress_region_id(String.valueOf(adressRegion.getId()));
        changeOperator.setOperator_com_name(operator.getName());
        changeOperator.setOperator_id(String.valueOf(operator.getId()));
        if(operator.getParent() != null){
            changeOperator.setOperator_parent_id(String.valueOf(operator.getParent().getId()));
        }else {
            changeOperator.setOperator_parent_id("0");
        }
        changeOperator.setPlatform(commonConfig.getValue());
        log.info("changeOperator:" + JSONObject.toJSONString(changeOperator));
        Result result = containerService.changeOperator(changeOperator);
        log.info("update operator result:" + result);
        if(Objects.equals(result.getCode(), 0)){
            log.error("update operator result:" + result);
            throw new NbiotException(600007, OperatorExceptionEnum.E_0008.getMessage());
        }
    }

    /**
     * 通知第三方发货平台删除运营公司
     */
    private void broadcastToThirdPlatformDelete(Integer id) {
        TcommonConfig commonConfig = commonService.findByName(CommonConstant.CURRENT_ENV_KEY);
        if (commonConfig == null){
            throw new NbiotException(501, "no current-env config find! please config it.");
        }
        Result result = containerService.deleteOperator(String.valueOf(id), commonConfig.getValue());
        log.info("delete operator result:" + result);
        if(Objects.equals(result.getCode(), 0)){
            log.error("delete operator result:" + result);
            throw new NbiotException(600008, OperatorExceptionEnum.E_0009.getMessage());
        }
    }
}
