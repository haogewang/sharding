package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.*;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.DecyptTokenUtil;
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
    private ReservationService reservationService;
    @Autowired
    private InstallSiteService installSiteService;
    @Autowired
    private PolicyInfoService policyInfoService;

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
    public List<TnoTrackerElec> findByUserId(String userId) {
        List<Long> ids = elecmobileUserService.findNoTrackerElecIdByUserId(userId);
        if (ids != null && ids.size() > 0) {
            List<TnoTrackerElec> noTrackerElecs = noTrackerElecRepository.findByIds(ids);
            return noTrackerElecs;
        }
        return null;
    }

    @Override
    public Integer add(NotrackerRegister notrackerRegister, HttpServletRequest request) {
        log.info("notracker-Register:" + JSONObject.toJSONString(notrackerRegister));
        try {
            Tuser tuser = notrackerRegister.getUser();
            Telectrmobile electrombile = notrackerRegister.getElec();
            String reservationNo = notrackerRegister.getReservationNo();
            if (tuser == null || StringUtils.isEmpty(tuser.getId())) {
                //UserExceptionEnum.E_0003.getMessage()
                throw new NbiotException(200001, "参数错误");
            }
            if (electrombile == null || StringUtils.isEmpty(electrombile.getPlateNumber())) {
                //ElectrombileExceptionEnum.E_0004
                throw new NbiotException(200002, "用户不存在");
            }
            validUser(tuser.getId());
            tuser = userService.updateUser(tuser);
            if (StringUtils.isNotEmpty(electrombile.getImei())) {
                Tregistration register = new Tregistration();
                register.setElectrmobile(electrombile);
                register.setUser(tuser);
                register.setReservationNo(reservationNo);
                registrationService.register(register, false);
                if(tuser.getOperatorId() == null || tuser.getOperatorId() == 0){
                    TdeviceInventory device = deviceInventoryService.findByImei(electrombile.getImei());
                    updateUser(tuser, device);
                }
                return 1;
            } else {
                Integer workerOperatorId = DecyptTokenUtil.getOperatorId(request);
                if (workerOperatorId == null) {
                    log.error("no right. worker operatorId is null.");
                    throw new NbiotException(600011, OperatorExceptionEnum.E_00012.getMessage());
                }
                registerationServiceImpl.validElecPlateNo(electrombile.getPlateNumber());
                registerationServiceImpl.validElecmobileVin(electrombile.getVin());
                TnoTrackerElec tnoTrackerElec = createNotrackerElec(electrombile, tuser);
                log.debug("");
                saveNoTrackerElecUserRelationShip(tuser, tnoTrackerElec, workerOperatorId);
                deleteNoTrackerRedisKey();
                if (StringUtils.isNotEmpty(notrackerRegister.getReservationNo())) {
                    reservationService.deleteByReserNo(notrackerRegister.getReservationNo());
                }
                return 1;
            }
        } catch (Exception e) {
            log.error("create noTrackerElec error", e);
            //RegisterExceptionEnum.E_0005.getCode()
            throw new NbiotException(10000002, "");
        }
    }

    @Override
    public String addUserAndElecmobile(NotrackerRegister notrackerRegister, HttpServletRequest request) {
        Tuser tuser = notrackerRegister.getUser();
        Telectrmobile electrombile = notrackerRegister.getElec();
        if (tuser == null || StringUtils.isEmpty(tuser.getPhone())) {
            //UserExceptionEnum.E_0006
            throw new NbiotException(200004, "参数错误");
        }
        if (electrombile == null || StringUtils.isEmpty(electrombile.getPlateNumber())) {
            throw new NbiotException(3000002, "");
        }
        Tuser user = userService.findByPhone(tuser.getPhone());
        if (user != null) {
            log.error("use has exist.phone:" + tuser.getPhone());
            //UserExceptionEnum.E_0007
            throw new NbiotException(200005, "该账号已注册");
        }
        Integer workerOperatorId = DecyptTokenUtil.getOperatorId(request);
        if (workerOperatorId == null) {
            log.error("worker operatorId is null.");
            throw new NbiotException(600011, OperatorExceptionEnum.E_00012.getMessage());
        }
        registerationServiceImpl.validElecPlateNo(electrombile.getPlateNumber());
        registerationServiceImpl.validElecmobileVin(electrombile.getVin());
        tuser = registerationServiceImpl.createUser(null, tuser);
        registerationServiceImpl.userPush(tuser);
        TnoTrackerElec tnoTrackerElec = createNotrackerElec(electrombile, tuser);
        saveNoTrackerElecUserRelationShip(tuser, tnoTrackerElec, workerOperatorId);
        deleteNoTrackerRedisKey();
        if (StringUtils.isNotEmpty(notrackerRegister.getReservationNo())) {
            reservationService.deleteByReserNo(notrackerRegister.getReservationNo());
        }
        return tuser.getId();
    }

    @Override
    public Long boundDevice(DeviceBound deviceBound) {
        TdeviceInventory device = deviceInventoryService.findByImei(deviceBound.getImei());
        Tuser user = validUser(deviceBound.getUserId());
        //310绑定设备
        if (CommonConstant.DEVICE_MODE_310.equals(deviceBound.getType())) {
            Long id = register310(deviceBound, device, user);
            return id;
        }
        throw new NbiotException(DeviceExceptionEnum.E_00037.getCode(), DeviceExceptionEnum.E_00037.getMessage());
    }

    @Override
    public Integer unBoundDevice(String imei) {
        Tregistration register = registrationService.findByImei(imei);
        if(register != null){
            registrationService.deleteRegistration(register.getRegisterId(), imei, false);
            return 1;
        }
        else{
            log.error("unbound device is not found.imei:" + imei);
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public Integer unBoundByElecIdAndUserId(String userId, Long elecId) {
        List<TelectrombileUser> elecUsers = elecmobileUserService.findByUserId(userId);
        if (elecUsers.isEmpty()) {
            log.error("no elec found.userId:" + userId);
            //UserExceptionEnum.E_0008.getCode()
            throw new NbiotException(200006, "该用户无电动车");
        }
        Telectrmobile electrmobile = electrombileService.findByElecId(elecId);
        if(electrmobile == null){
            log.error("elecId is null.elecId:" + elecId);
            //ElectrombileExceptionEnum.E_0009
            throw new NbiotException(3000006, "");
        }
        for (TelectrombileUser elecUser : elecUsers) {
            if (elecUser.getElectrombileId() != null && elecUser.getElectrombileId().equals(elecId)) {
                log.info("has imei.delete elecId:" + elecId);
                Telectrmobile electrombile = electrombileService.findByElecId(elecId);
                if (electrombile != null) {
                    unBoundDevice(electrombile.getImei());
                }
                break;
            }
            if (elecUser.getNoTrackerElecId() != null && elecUser.getNoTrackerElecId().equals(elecId)) {
                log.info("no imei.delete elecId:" + elecId);
                TnoTrackerElec tnoTrackerElec = findById(elecId);
                deleteById(elecId);
                break;
            }
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
                //RegisterExceptionEnum.E_0002.getMessage()
                throw new NbiotException(10000001, "");
            }
            Telectrmobile telectrombile = electrombileService.findByImei(imei);
            if (telectrombile == null) {
                throw new NbiotException(DeviceExceptionEnum.E_0000.getCode(), DeviceExceptionEnum.E_0000.getMessage());
            }
            Tuser user = userService.findById(register.getUserId());
            TnoTrackerElec tnoTrackerElec = createNotrackerElec(telectrombile, user);
            List<TelectrombileUser> elecUsers = elecmobileUserService.findByElecId(telectrombile.getElectrmobileId());
            if (elecUsers != null && !elecUsers.isEmpty()) {
                TelectrombileUser elecuser = elecUsers.get(0);
                elecuser.setOperatorId(null);
                elecuser.setElectrombileId(null);
                elecuser.setNoTrackerElecId(tnoTrackerElec.getId());
                elecmobileUserService.update(elecuser);
            }
            registrationService.deleteRegistration(register.getRegisterId(), imei, false);
            deleteNoTrackerRedisKey();
        } catch (Exception e) {
            log.error("unboundDeviceNoDeleteElec error.", e);
            throw new NbiotException(400001, "");
        }
        return j;
    }

    @Override
    public Integer deleteById(Long id) {
        TnoTrackerElec noTrackerElec = findById(id);
        if (noTrackerElec != null) {
            Integer i = elecmobileUserService.deleteByNoTrackerElecId(noTrackerElec.getId());
            log.info("delete elecmobileUser success. i:" + i);
        }
        noTrackerElecRepository.deleteById(id);
        deleteNoTrackerRedisKey();
        return 1;
    }

    @Override
    public Integer deleteByUserId(String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer update(TnoTrackerElec tnoTrackerElec) {
        TnoTrackerElec elec = findById(tnoTrackerElec.getId());
        if (elec == null) {
            //ElectrombileExceptionEnum.E_0009
            throw new NbiotException(3000006, "");
        }
        BeanUtils.copyProperties(tnoTrackerElec, elec, PropertyUtil.getNullProperties(tnoTrackerElec));
        noTrackerElecRepository.save(elec);
        return 1;
    }

    private Long register310(DeviceBound deviceBound, TdeviceInventory device, Tuser user) {
        Telectrmobile electrombile = create310ElectrmobileData(deviceBound, device, user);
        Tregistration register = new Tregistration();
        register.setElectrmobile(electrombile);
        register.setUser(user);
        Tregistration newRegister = registrationService.register(register, false);
        if(user.getOperatorId() == null || user.getOperatorId() == 0){
            updateUser(user, device);
        }
        boundPolicyToUser(device.getImei(), user.getId());
        return newRegister.getRegisterId();
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
        String random = String.format("%07d", new Random().nextInt(1000000) + 1);
        if (StringUtils.isEmpty(electrombile.getVin())) {
            electrombile.setVin(random);
        }
        return electrombile;
    }

    private void setElectrmobileData(Telectrmobile electrombile, TdeviceInventory device, Tuser newUser) {
        electrombile.setImei(device.getImei());
        RegisterationServiceImpl.setElecValue(electrombile, device, newUser, installSiteService);
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

    private void saveNoTrackerElecUserRelationShip(Tuser newUser, TnoTrackerElec tnoTrackerElec, Integer operatorId) {
        String userId = newUser.getId();
        TelectrombileUser electrombileUser = new TelectrombileUser();
        electrombileUser.setNoTrackerElecId(tnoTrackerElec.getId());
        electrombileUser.setUserId(userId);
        electrombileUser.setOperatorId(operatorId);
        elecmobileUserService.save(electrombileUser);
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
