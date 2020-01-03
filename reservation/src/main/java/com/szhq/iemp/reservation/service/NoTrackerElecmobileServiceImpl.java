package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DencryptTokenUtil;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.service.*;
import com.szhq.iemp.reservation.api.vo.DeviceBound;
import com.szhq.iemp.reservation.api.vo.NotrackerRegister;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.reservation.repository.NoTrackerElecRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@Transactional
@Slf4j
@CacheConfig(cacheNames = "aepNoTrackerElec")
public class NoTrackerElecmobileServiceImpl implements NoTrackerElecmobileService {
    @Value("${spring.profiles.active}")
    private String active;
    @Resource
    private NoTrackerElecRepository noTrackerElecRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private ElecmobileUserService elecmobileUserService;
    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private RegisterationServiceImpl registerationServiceImpl;
    @Autowired
    private InstallSiteService installSiteService;
    @Autowired
    private PolicyInfoService policyInfoService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private NbiotDeviceInfoService nbiotDeviceInfoService;

    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;

    @Cacheable(unless = "#result == null|| #result.getTotal() == 0")
    @Override
    public MyPage<TnoTrackerElec> findAllByCriteria(Integer page, Integer size, String sorts, String orders, ElecmobileQuery elequery) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TnoTrackerElec> pages = noTrackerElecRepository.findAll(new Specification<TnoTrackerElec>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TnoTrackerElec> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                Join<TnoTrackerElec, Tuser> joinUser = root.join("user", JoinType.LEFT);
                if (elequery != null) {
                    if (StringUtils.isNotEmpty(elequery.getPlateNo())) {
                        list.add(criteriaBuilder.equal(root.get("plateNumber").as(String.class), elequery.getPlateNo()));
                    }
                    if (StringUtils.isNotEmpty(elequery.getPhone())) {
                        list.add(criteriaBuilder.equal(root.get("user").get("phone").as(String.class), elequery.getPhone()));
                    }
                    if (StringUtils.isNotEmpty(elequery.getOwnerName())) {
                        list.add(criteriaBuilder.like(root.get("user").get("name").as(String.class), "%" + elequery.getOwnerName() + "%"));
                    }
                    if (elequery.getOperatorIdList() != null) {
                        list.add(joinUser.get("operatorId").as(Integer.class).in(elequery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TnoTrackerElec>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public MyPage<TnoTrackerElec> findAllByCretia(Integer page, Integer size, String sorts, String orders, ElecmobileQuery eleQuery) {
        Sort sort = SortUtil.sort(sorts, orders);
        Pageable pageable = PageRequest.of(page, size, sort);
        if (eleQuery.getOperatorIdList() == null) {
            Page<TnoTrackerElec> pages = noTrackerElecRepository.findByCretiaNoOperatorIds(eleQuery.getPhone(), eleQuery.getPlateNo(), eleQuery.getOwnerName(), pageable);
            return new MyPage<TnoTrackerElec>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
        }
        Page<TnoTrackerElec> pages = noTrackerElecRepository.findByCretia(eleQuery.getPhone(), eleQuery.getPlateNo(), eleQuery.getOwnerName(), eleQuery.getOperatorIdList(), pageable);
        return new MyPage<TnoTrackerElec>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }


    @Override
    public TnoTrackerElec findByPlateNumber(String plateNo) {
        return noTrackerElecRepository.findByPlateNumber(plateNo);
    }

    @Override
    public TnoTrackerElec findByPlateNumberAndOperatorIdsIn(String plateNo, List<Integer> operatorIdList) {
        return noTrackerElecRepository.findByPlateNumberAndOperatorIdsIn(plateNo, operatorIdList);
    }

    @Override
    public TnoTrackerElec findByVin(String vin) {
        return noTrackerElecRepository.findByVin(vin);
    }

    @Override
    public TnoTrackerElec findById(Long id) {
        return noTrackerElecRepository.findById(id).orElse(null);
    }

    @Override
    public Long add(NotrackerRegister notrackerRegister, HttpServletRequest request) {
        log.info("notracker-Register:" + JSONObject.toJSONString(notrackerRegister));
//        try {
            Tuser tuser = notrackerRegister.getUser();
            Telectrmobile electrombile = notrackerRegister.getElec();
            String reservationNo = notrackerRegister.getReservationNo();
            if (tuser == null || StringUtils.isEmpty(tuser.getId())) {
                throw new NbiotException(200001, "用户Id不能为空");
            }
            if (electrombile == null || StringUtils.isEmpty(electrombile.getPlateNumber())) {
                throw new NbiotException(3000002, "电动车车牌不能为空");
            }
            validUser(tuser.getId());
            tuser = userService.updateUser(tuser);
            //有设备
            if (StringUtils.isNotEmpty(electrombile.getImei())) {
                Tregistration register = new Tregistration();
                register.setElectrombile(electrombile);
                register.setUser(tuser);
                register.setReservationNo(reservationNo);
                Tregistration tregistration = registrationService.register(register, false);
                if(tuser.getOperatorId() == null || tuser.getOperatorId() == 0){
                    TdeviceInventory device = deviceInventoryService.findByImei(electrombile.getImei());
                    updateUser(tuser, device);
                }
                return tregistration.getRegisterId();
            }
            //无设备
            else {
                Integer workerOperatorId = DencryptTokenUtil.getOperatorId(request);
                if (workerOperatorId == null) {
                    log.error("no right. worker operatorId is null.");
                    throw new NbiotException(600011, OperatorExceptionEnum.E_00012.getMessage());
                }
                registerationServiceImpl.validElecPlateNo(electrombile.getPlateNumber());
                registerationServiceImpl.validElecmobileVin(electrombile.getVin());
                Tregistration register = new Tregistration();
                register.setElectrombile(electrombile);
                register.setUser(tuser);
                Toperator operator = operatorService.findById(workerOperatorId);
                if(operator == null){
                    log.error("operator is null.operatorId:" + workerOperatorId);
                    throw new NbiotException(500, "");
                }
                register.setOperator(operator);
                register.setReservationNo(reservationNo);
                Tregistration tregistration = registrationService.register(register, false);
                return tregistration.getRegisterId();
            }
//        } catch (Exception e) {
//            log.error("create noTrackerElec error", e);
//            throw new NbiotException(10000002, RegisterExceptionEnum.E_0005.getMessage());
//        }
    }

    @Override
    public String addUserAndElecmobile(NotrackerRegister notrackerRegister, HttpServletRequest request) {
        Tuser tuser = notrackerRegister.getUser();
        Telectrmobile electrombile = notrackerRegister.getElec();
        if (tuser == null || StringUtils.isEmpty(tuser.getPhone())) {
            throw new NbiotException(200004, "参数错误");
        }
        if (electrombile == null || StringUtils.isEmpty(electrombile.getPlateNumber())) {
            throw new NbiotException(3000002, "");
        }
        Tuser user = userService.findByPhone(tuser.getPhone());
        if (user != null) {
            log.error("use has exist.phone:" + tuser.getPhone());
            throw new NbiotException(200005, "该账号已注册");
        }
        Integer workerOperatorId = DencryptTokenUtil.getOperatorId(request);
        if (workerOperatorId == null) {
            log.error("worker operatorId is null.");
            throw new NbiotException(600011, OperatorExceptionEnum.E_00012.getMessage());
        }
        Tregistration register = new Tregistration();
        register.setUser(tuser);
        register.setElectrombile(electrombile);
        Toperator operator = operatorService.findById(workerOperatorId);
        if(operator == null){
            log.error("operator is not found.operatorId:" + workerOperatorId);
            throw new NbiotException(500, "");
        }
        register.setOperator(operator);
        register.setReservationNo(notrackerRegister.getReservationNo());
        Tregistration result = registrationService.register(register, true);
        if(result != null){
            TdeviceInventory device = new TdeviceInventory();
            device.setOperatorId(workerOperatorId);
            updateUser(result.getUser(), device);
        }
        return tuser.getId();
    }

    @Override
    public Long boundDevice(DeviceBound deviceBound) {
        TdeviceInventory device = deviceInventoryService.findByImei(deviceBound.getImei());
        Tuser user = validUser(deviceBound.getUserId());
        if(StringUtils.isNotEmpty(deviceBound.getType()) && !deviceBound.getType().equals(device.getModelNo())){
            log.error("wrong parameter.device modelNo wrong.imei:" + deviceBound.getImei());
            throw new NbiotException(400023, DeviceExceptionEnum.E_00037.getMessage());
        }
        //310绑定设备
        if (CommonConstant.DEVICE_MODE_310.equals(deviceBound.getType()) || CommonConstant.DEVICE_MODE_310.equals(device.getModelNo())) {
            return register310(deviceBound, device, user);
        }
        //302绑定设备
        if (CommonConstant.DEVICE_MODE_302.equals(device.getModelNo())) {
            if(StringUtils.isEmpty(deviceBound.getPlateNumber())){
                log.error("boundDevice error,plateNo is must input!");
                throw new NbiotException(3000002, "");
            }
            Tregistration register = registrationService.findByPlateNo(deviceBound.getPlateNumber());
            if(register == null){
                log.error("no register found.plateNo:" + deviceBound.getPlateNumber());
                throw new NbiotException(10000007, "该备案信息不存在");
            }
            if(StringUtils.isNotEmpty(register.getImei())){
                log.error("the electrmobile has bound devices.imei:" + register.getImei());
                throw new NbiotException(3000011, "该车已绑定设备");
            }
            Telectrmobile electrmobile = electrombileService.findByElecId(register.getElectrmobileId());
            if(electrmobile != null){
                electrmobile = createElecmobileData(electrmobile, device, user);
                electrombileService.save(electrmobile);
                redisUtil.del(CommonConstant.ELEC_ID + electrmobile.getElectrmobileId());
                log.info("update elecmobile success.elecId:" + electrmobile.getElectrmobileId());
                updateElecUserRelationShip(electrmobile, device.getOperatorId());
            }
            deviceInventoryService.updateDevStat(device.getImei(), 1);
            registerationServiceImpl.saveDeviceInfo(device);
            registerationServiceImpl.addSaleRecord(device);
            if(user.getOperatorId() == null || user.getOperatorId() == 0){
                updateUser(user, device);
            }
            if(!"kfyd".equals(active)){
                registerationServiceImpl.sendRedisInfo(device, deviceBound.getImei());
            }
            boundPolicyToUser(device.getImei(), user.getId());
            return updateRegister(device, register);
        }
        throw new NbiotException(400023, DeviceExceptionEnum.E_00037.getMessage());
    }

    @Override
    public Integer unBoundDevice(String imei) {
        Tregistration register = registrationService.findByImei(imei);
        if(register != null){
            registrationService.deleteRegistration(register.getRegisterId(), imei, false);
            return 1;
        }
        else{
            log.error("unbound failed. device is not found.imei:" + imei);
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public Integer unBoundByElecIdAndUserId(String userId, Long elecId) {
        List<TelectrombileUser> elecUsers = elecmobileUserService.findByUserId(userId);
        if (elecUsers.isEmpty()) {
            log.error("no elec found.userId:" + userId);
            throw new NbiotException(200006, "该用户无电动车");
        }
        Telectrmobile electrmobile = electrombileService.findByElecId(elecId);
        if(electrmobile == null){
            log.error("elecId is null.elecId:" + elecId);
            throw new NbiotException(3000006, "");
        }
        if(StringUtils.isNotEmpty(electrmobile.getImei())){
            log.info("has imei.delete elecId:" + elecId);
            unBoundDevice(electrmobile.getImei());
        }
        else{
            Long elecmobileId = electrmobile.getElectrmobileId();
            electrombileService.deleteByElecId(elecmobileId);
            registrationService.deleteByElecId(elecmobileId);
            elecmobileUserService.deleteByElecId(elecmobileId);
            registerationServiceImpl.deleteRegisterAndElecRedisKey();
            delRedisKeyByElec(elecmobileId);
            redisUtil.del(CommonConstant.REGISTER_USERID + userId);
        }
        return 1;
    }

    @Override
    public Integer unboundDeviceNoDeleteElec(String imei) {
        int j = 0;
        try {
            Tregistration register = registrationService.findByImei(imei);
            if(register == null){
                log.error("device is not register.imei:" + imei);
                throw new NbiotException(10000001, "该备案信息不存在");
            }
            Telectrmobile electrombile = electrombileService.findByImei(imei);
            if (electrombile == null) {
                throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
            }
            if(CommonConstant.DEVICE_MODE_302.equals(register.getModelNo())){
                updateElectrmobileToNull(electrombile);
                updateElecUserOperatorToNull(electrombile.getElectrmobileId(), register.getUserId());
                updateRegisterToNull(register);
                deviceInventoryService.updateDevStat(imei, 2);
                nbiotDeviceInfoService.deleteByImei(imei);
                registerationServiceImpl.deleteOldImeiData(imei);
                registerationServiceImpl.deleteRegisterAndElecRedisKey();
                registerationServiceImpl.saveDeleteRegisterLog(register);
                redisUtil.del(CommonConstant.ELEC_ID + electrombile.getElectrmobileId());
                redisUtil.del(CommonConstant.REGISTER_USERID + register.getUserId());
                redisUtil.del(CommonConstant.DEVICE_IMEI + imei);
            }
            if(CommonConstant.DEVICE_MODE_310.equals(register.getModelNo())){
                registrationService.deleteRegister(register.getRegisterId());
            }
        } catch (Exception e) {
            log.error("unboundDeviceNoDeleteElec error.", e);
            throw new NbiotException(400001, "解绑失败");
        }
        return j;
    }

    /**
     * 310备案
     */
    private Long register310(DeviceBound deviceBound, TdeviceInventory device, Tuser user) {
        Telectrmobile electrombile = create310ElectrmobileData(deviceBound, device, user);
        Tregistration register = new Tregistration();
        register.setElectrombile(electrombile);
        register.setUser(user);
        Tregistration newRegister = registrationService.register(register, false);
        if(user.getOperatorId() == null || user.getOperatorId() == 0){
            updateUser(user, device);
        }
        boundPolicyToUser(device.getImei(), user.getId());
        return newRegister.getRegisterId();
    }

    /**
     * 修改备案
     */
    private Long updateRegister(TdeviceInventory device, Tregistration register) {
        if(register != null && device != null){
            register.setIsp(device.getIsp());
            register.setImei(device.getImei());
            register.setOperatorId(device.getOperatorId());
            register.setInstallSiteId(device.getInstallSiteId());
            register.setInstallSiteName(device.getInstallSiteName());
            register.setModelNo(device.getModelNo());
        }
        register = registrationService.save(register);

        registerationServiceImpl.deleteRegisterAndElecRedisKey();
        return register.getRegisterId();
    }

    private void boundPolicyToUser(String imei, String userId) {
        TpolicyInfo policyInfo = policyInfoService.findByImeiAndUserId(imei, userId);
        if(policyInfo != null){
            return;
        }
        else {
            policyInfo = policyInfoService.findByImeiAndUserIdisNull(imei);
            if(policyInfo != null){
                policyInfo.setUserId(userId);
                policyInfoService.save(policyInfo);
            }
        }
    }

    private Telectrmobile create310ElectrmobileData(DeviceBound deviceBound, TdeviceInventory device, Tuser user) {
        Telectrmobile electrombile = new Telectrmobile();
        electrombile.setName(deviceBound.getDeviceName());
        electrombile.setFrequency(deviceBound.getFrequency());
        electrombile = createElecmobileData(electrombile, device, user);
        return electrombile;
    }

    private Telectrmobile createElecmobileData(Telectrmobile electrombile, TdeviceInventory device, Tuser newUser) {
        setElectrmobileData(electrombile, device, newUser);
        if (StringUtils.isEmpty(electrombile.getPlateNumber())) {
            electrombile.setPlateNumber(device.getImei());
        }
        if (StringUtils.isEmpty(electrombile.getImei())) {
            electrombile.setImei(device.getImei());
        }
        String random = String.format("%08d", new Random().nextInt(1000000000) + 1);
        String random2 = String.format("%08d", new Random().nextInt(10000000) + 1);
        if (StringUtils.isEmpty(electrombile.getVin())) {
            electrombile.setVin(random + random2);
        }
        return electrombile;
    }

    private void setElectrmobileData(Telectrmobile electrombile, TdeviceInventory device, Tuser newUser) {
        electrombile.setImei(device.getImei());
        RegisterationServiceImpl.setElecValue(electrombile, device, newUser, installSiteService, null);
    }

    /**
     * 验证用户信息
     */
    private Tuser validUser(String userId) {
        Tuser user = userService.findById(userId);
        if (user == null) {
            log.error("valid user error.user is not exist. userId:" + userId);
            //UserExceptionEnum.E_0004
            throw new NbiotException(200002, "用户不存在");
        }
        return user;
    }

    /**
     * 修改用户信息
     */
    private Tuser updateUser(Tuser user, TdeviceInventory deviceInventory) {
        log.info("before update user:" + JSONObject.toJSONString(user));
        if (deviceInventory.getStorehouseId() != null) {
            user.setDeviceStorehouseId(deviceInventory.getStorehouseId());
        }
        if (deviceInventory.getInstallSiteId() != null) {
            user.setInstallSiteId(deviceInventory.getInstallSiteId());
        }
        user.setIotTypeId(deviceInventory.getIotTypeId());
        user.setOperatorId(deviceInventory.getOperatorId());
        if (deviceInventory.getRegionId() != null) {
            user.setResidentId(deviceInventory.getRegionId());
        }
        try {
            user = userService.add(user);

        } catch (Exception e) {
            log.error("update user error", e);
            throw new NbiotException(200003, "修改用户信息失败");
        }
        return user;
    }

    /**
     * 创建电动车
     */
    private TnoTrackerElec createNotrackerElec(Telectrmobile electrombile, Tuser user) {
        TnoTrackerElec noTrackerElec = new TnoTrackerElec();
        BeanUtils.copyProperties(electrombile, noTrackerElec, PropertyUtil.getNullProperties(electrombile));
        noTrackerElec.setUser(user);
        TnoTrackerElec tnoTrackerElec = noTrackerElecRepository.save(noTrackerElec);
        return tnoTrackerElec;
    }

    private void updateElecUserRelationShip(Telectrmobile electrmobile, Integer operatorId) {
        Long elecId = electrmobile.getElectrmobileId();
        List<TelectrombileUser> electrombileUsers = elecmobileUserService.findByElecId(elecId);
        if(electrombileUsers != null && !electrombileUsers.isEmpty()){
            for(TelectrombileUser electrombileUser : electrombileUsers){
                electrombileUser.setOperatorId(operatorId);
                elecmobileUserService.save(electrombileUser);
            }
        }
    }

    private void delRedisKeyByElec(Long elecId) {
        String key1 = CommonConstant.ELECALL_ID_KEY + "*" + elecId + "*";
        Set<String> sets1 = redisUtil.keys(key1);
        if (sets1 != null && sets1.size() > 0) {
            for (String s : sets1) {
                log.info("del redis key [" + s + "]");
                redisUtil.del(s);
            }
        }
    }

    private void updateRegisterToNull(Tregistration register) {
        register.setImei(null);
        register.setInstallSiteId(null);
        register.setInstallSiteName(null);
        register.setIsp(null);
        register.setModelNo(null);
        registrationService.save(register);
        log.info("update register to null success.");
    }

    private void updateElecUserOperatorToNull(Long elecId, String userId) {
       TelectrombileUser electrombileUser = elecmobileUserService.findByUserIdAndElecId(userId, elecId);
       electrombileUser.setOperatorId(null);
       elecmobileUserService.save(electrombileUser);
        log.info("update elec-user to null success.");
    }

    private void updateElectrmobileToNull(Telectrmobile electrombile) {
        electrombile.setImei(null);
        electrombile.setPoliceId(null);
        electrombile.setPoliceName(null);
        electrombile.setDevname(null);
        electrombile.setDevtype(null);
        electrombile.setInstallSiteName(null);
        electrombile.setInstallSiteId(null);
        electrombile.setManufactorName(null);
        electrombile.setManufactorId(null);
        electrombile.setRegionId(null);
        electrombile.setRegionName(null);
        electrombile.setStorehouseName(null);
        electrombile.setStorehouseId(null);
        electrombile.setModelNo(null);
        electrombile.setIotTypeName(null);
        electrombile.setIotTypeId(null);
        electrombileService.save(electrombile);
        log.info("update elec to null success.");
    }

    @Override
    public Integer deleteNoTrackerRedisKey() {
        int count = 0;
        if (redisUtil.keys(CommonConstant.NOTRACKER_PATTERN) != null && redisUtil.keys(CommonConstant.NOTRACKER_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.NOTRACKER_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
                count++;
            }
        }
        return count;
    }

}
