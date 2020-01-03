package com.szhq.iemp.reservation.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.*;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.*;
import com.szhq.iemp.common.vo.ExportExcelData;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.service.*;
import com.szhq.iemp.reservation.api.vo.TregisterView;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import com.szhq.iemp.reservation.repository.RegistrationRepository;
import com.szhq.iemp.reservation.repository.TroleRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import dot.server.api.DotService;
import dot.server.common.model.Result;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "aepregister")
public class RegisterationServiceImpl implements RegistrationService {

    @Value("${spring.profiles.active}")
    private String active;
    @Value("${lc-register-url}")
    private String lcRegisterUrl;
    @Value("${lc-update-register-url}")
    private String lcupdateRegisterUrl;
    @Value("${lc-changeimei-url}")
    private String lcChangeImeiUrl;
    @Value("${lc-delte-register-url}")
    private String lcDeleteRegisterUrl;
    @Reference
    private DotService dotService;

    @Resource
    private RegistrationRepository registerRepository;
    @Resource
    private TroleRepository roleRepository;
    @Autowired
    private CommonService commonService;
    @Autowired
    private NbiotDeviceInfoService nbiotDeviceInfoService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private RegistrationLogService registrationLogService;
    @Autowired
    private ElecmobileUserService elecmobileUserService;
    @Autowired
    private NbiotDeviceDataService nbiotTrackerDataService;
    @Autowired
    private NbiotHistoryWlanDataService nbiotHistoryWlanDataService;
    @Autowired
    private NbiotDeviceRtDataService nbiotDeviceRtDataService;
    @Autowired
    private EsTrackvelServiceImpl esTrackvelService;
    @Autowired
    private EsNbiotDeviceAlarmService esNbiotDeviceAlarmService;
    @Autowired
    private NbiotDeviceAlarmRtDataService nbiotDeviceAlarmRtDataService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private UserPushService userPushService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private DeviceStateService deviceStateService;
    @Autowired
    private InstallSiteService installSiteService;
    @Autowired
    private AddressRegionService regionService;
    @Autowired
    private PolicePrecinctService policePrecinctService;
    @Autowired
    private SaleRecordService saleRecordService;
    @Autowired
    private DeviceStoreHouseService storeHouseService;
    @Autowired
    private PolicyInfoService policyInfoService;

    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;
    @Resource(name = "secondRedisUtil")
    private RedisUtil secondRedisUtil;
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Tregistration register(Tregistration data, Boolean isCreateUser) {
        if (data == null || data.getElectrombile() == null || data.getUser() == null) {
            log.error("wrong parameter." + JSONObject.toJSONString(data));
            throw new NbiotException(400, "");
        }
        Telectrmobile electrombile = data.getElectrombile();
        Tuser user = data.getUser();
        if (StringUtils.isNotEmpty(electrombile.getImei()) && findByImei(electrombile.getImei()) != null) {
            log.error("imei:" + electrombile.getImei() + " has registered!");
            throw new NbiotException(10000005, "");
        }
        validElecmobileVin(electrombile.getVin());
        validElecPlateNo(electrombile.getPlateNumber());
        validUserPhone(user.getPhone(), isCreateUser);
        log.info("+++++imei+++++" + electrombile.getImei());
        //无设备
        if(StringUtils.isEmpty(electrombile.getImei())){
            Tuser newUser = null;
            if (isCreateUser && StringUtils.isEmpty(user.getId())) {
                log.info("create new user.phone:" + user.getPhone());
                newUser = createUser(null, user);
                userPush(newUser);
            }
            else {
                if (StringUtils.isEmpty(user.getId())) {
                    log.error("userId is null. phone:" + user.getPhone());
                    throw new NbiotException(200001, "用户Id不能为空");
                }
                validUser(user.getId());
                newUser = userService.updateUser(user);
            }
            Telectrmobile newElectrmobile = createElecmobile(electrombile, null, newUser, data.getOperator());
            Tregistration registration = saveRegister(newElectrmobile, newUser, null, data.getPayNumber());
            if(data.getOperator() != null){
                createElecUserRelationShip(newUser.getId(), newElectrmobile.getElectrmobileId(), data.getOperator().getId());
            }else{
                log.error("data.getOperator is null.userId:" + newUser.getId());
                throw new NbiotException(400, "参数错误");
            }
            deleteRegisterAndElecRedisKey();
            if (StringUtils.isNotEmpty(data.getReservationNo())) {
                reservationService.deleteByReserNo(data.getReservationNo());
            }
            return registration;
        }
        //有设备
        else{
            TdeviceInventory deviceInventory = deviceInventoryService.findByImei(electrombile.getImei());
            if (deviceInventory == null) {
                log.error("device is not found.imei:" + electrombile.getImei());
                throw new NbiotException(10000004, "设备不存在");
            }
            if(!CommonConstant.DEVICE_MODE_310.equals(deviceInventory.getModelNo())){
                if(deviceInventory.getInstallSiteId() == null){
                    log.error("site is null.imei:" + electrombile.getImei());
                    throw new NbiotException(10000004, "未入库、已入库未分配的设备不可以注册");
                }
            }
            Tuser newUser = null;
            if (isCreateUser && StringUtils.isEmpty(user.getId())) {
                log.info("create new user.phone:" + user.getPhone());
                newUser = createUser(deviceInventory, user);
                userPush(newUser);
            } else {
                if (StringUtils.isEmpty(user.getId())) {
                    log.error("userId is null, phone:" + user.getPhone());
                    throw new NbiotException(200001, "用户Id不能为空");
                }
                validUser(user.getId());
                newUser = userService.updateUser(user);
            }
            Telectrmobile newElectrombile = createElecmobile(electrombile, deviceInventory, newUser, null);
            Tregistration registration = saveRegister(newElectrombile, newUser, deviceInventory, data.getPayNumber());
            createElecUserRelationShip(newUser.getId(), newElectrombile.getElectrmobileId(), deviceInventory.getOperatorId());
            updateDeviceInventoryStatus(newElectrombile.getImei());
            saveDeviceInfo(deviceInventory);
            if(!"kfyd".equals(active)){
                sendRedisInfo(deviceInventory, newElectrombile.getImei());
            }
            if(CommonConstant.DEVICE_MODE_310.equals(deviceInventory.getModelNo())){
                saveDeviceState(newElectrombile.getImei(), deviceInventory);
                //310设备有激活特性的绑定不记录,只记录激活
                TdeviceStoreHouse storeHouse = storeHouseService.findById(deviceInventory.getStorehouseId());
                if(storeHouse != null && !storeHouse.getIsActive()){
                    addSaleRecord(deviceInventory);
                }
            }
            else{
                addSaleRecord(deviceInventory);
            }
            deleteRegisterAndElecRedisKey();
            if (StringUtils.isNotEmpty(data.getReservationNo())) {
                reservationService.deleteByReserNo(data.getReservationNo());
            }
            TcommonConfig config = commonService.findByName(CommonConstant.LC_BROADCAST_KEY);
            if (config != null && "true".equals(config.getValue())) {
                if (!"rfid".equals(deviceInventory.getDevtype()) || !CommonConstant.DEVICE_MODE_310.equals(deviceInventory.getModelNo())) {
                    broadcastRegister3TdPlatform(registration);
                }
            }
            return registration;
        }
    }

    @Override
    @Cacheable(unless = "#result == null || #result.getTotal() == 0")
    public MyPage<Tregistration> findRegistrationCriteria(Integer page, Integer size, String sorts, String orders, RegisterQuery aepQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "registerId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Tregistration> pages = registerRepository.findAll(new Specification<Tregistration>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Tregistration> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                List<Predicate> listor = new ArrayList<Predicate>();
                if (aepQuery != null) {
                    if (null != aepQuery.getCreateTime()) {
                        LocalDate localDate = aepQuery.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        localDate = localDate.plusDays(1);
                        Date endDate = new Date(java.sql.Date.valueOf(localDate).getTime() - 1L);
                        list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), aepQuery.getCreateTime(), endDate));
                    }
                    if (aepQuery.getStartTime() != null && aepQuery.getEndTime() != null) {
                        list.add(criteriaBuilder.between(root.get("createTime").as(Date.class), aepQuery.getStartTime(), aepQuery.getEndTime()));
                    }
                    if (StringUtils.isNotEmpty(aepQuery.getIdNumber())) {
                        list.add(criteriaBuilder.equal(root.get("idNumber").as(String.class), aepQuery.getIdNumber()));
                    }
                    if (StringUtils.isNotEmpty(aepQuery.getRealname())) {
                        list.add(criteriaBuilder.equal(root.get("username").as(String.class), aepQuery.getRealname()));
                    }
                    if (StringUtils.isNotEmpty(aepQuery.getPhone())) {
                        list.add(criteriaBuilder.equal(root.get("phone").as(String.class), aepQuery.getPhone()));
                    }
                    if (StringUtils.isNotEmpty(aepQuery.getPlateNumber())) {
                        list.add(criteriaBuilder.equal(root.get("plateNumber").as(String.class), aepQuery.getPlateNumber()));
                    }
                    if (StringUtils.isNotEmpty(aepQuery.getImei())) {
                        list.add(criteriaBuilder.equal(root.get("imei").as(String.class), aepQuery.getImei()));
                    }
                    if (aepQuery.getOwnerId() != null) {
                        list.add(criteriaBuilder.equal(root.get("userId").as(String.class), aepQuery.getOwnerId()));
                    }
                    if (aepQuery.getRegisterId() != null) {
                        list.add(criteriaBuilder.equal(root.get("registerId").as(Long.class), aepQuery.getRegisterId()));
                    }
                    if (aepQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(aepQuery.getOperatorIdList()));
                    }
                    if (StringUtils.isNotEmpty(aepQuery.getModelNo())) {
                        list.add(criteriaBuilder.equal(root.get("modelNo").as(String.class), aepQuery.getModelNo()));
                    }
                    if (aepQuery.getIsHaveImei() != null && aepQuery.getIsHaveImei()) {
                        list.add(criteriaBuilder.isNotNull(root.get("imei").as(String.class)));
                    }
                    if (aepQuery.getCustom() != null) {
                        Expression<String> phoneEx = root.get("phone").as(String.class);
                        Predicate p1 = criteriaBuilder.like(phoneEx, "%" + aepQuery.getCustom() + "%");
                        Expression<String> imeiEx = root.get("imei").as(String.class);
                        Predicate p2 = criteriaBuilder.like(imeiEx, "%" + aepQuery.getCustom() + "%");
                        Expression<String> plateEx = root.get("plateNumber").as(String.class);
                        Predicate p3 = criteriaBuilder.like(plateEx, "%" + aepQuery.getCustom() + "%");
                        listor.add(criteriaBuilder.or(p1));
                        listor.add(criteriaBuilder.or(p2));
                        listor.add(criteriaBuilder.or(p3));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                Predicate[] por = new Predicate[listor.size()];
                Predicate pred = criteriaBuilder.and(list.toArray(p));
                Predicate predicatesPermission = criteriaBuilder.or(listor.toArray(por));
//				return criteriaBuilder.and(list.toArray(p));
                if (listor.size() > 0) {
                    return query.where(pred, predicatesPermission).getRestriction();
                } else {
                    return criteriaBuilder.and(list.toArray(p));
                }
            }
        }, pageable);
        return new MyPage<Tregistration>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public Integer updateRegistration(Tregistration registration) {
        log.info("update registration id:" + registration.getRegisterId());
        Tregistration oldRegistration = registerRepository.findByRegisterId(registration.getRegisterId());
        if (oldRegistration == null) {
            throw new NbiotException(10000007, "该备案信息不存在");
        }
        Tregistration old = new Tregistration();
        BeanUtils.copyProperties(oldRegistration, old, PropertyUtil.getNullProperties(oldRegistration));
        Telectrmobile newElectrombile = registration.getElectrombile();
        Tuser newUser = registration.getUser();
        redisUtil.del(CommonConstant.ELEC_ID + oldRegistration.getElectrmobileId());
        Telectrmobile oldElectrmobile = electrombileService.findByElecId(oldRegistration.getElectrmobileId());
        Tuser oldUser = userService.findById(oldRegistration.getUserId());
        validUpdateRegisterParameter(oldElectrmobile, newElectrombile);
        if (null != oldElectrmobile) {
            String oldPlateNo = oldElectrmobile.getPlateNumber();
            if (StringUtils.isNotEmpty(newElectrombile.getPlateNumber()) && !(newElectrombile.getPlateNumber().equals(oldPlateNo))) {
                redisUtil.del(CommonConstant.ELEC_PLATENUMBER + oldPlateNo);
                log.info("delete redis elec-plate key, oldPlateNo:" + oldPlateNo);
                oldRegistration.setPlateNumber(newElectrombile.getPlateNumber());
            }
            BeanUtils.copyProperties(newElectrombile, oldElectrmobile, PropertyUtil.getNullProperties(newElectrombile));
            log.info("修改电动车信息:" + JSONObject.toJSONString(newElectrombile, SerializerFeature.DisableCircularReferenceDetect));
            electrombileService.save(oldElectrmobile);
            redisUtil.del(CommonConstant.ELEC_ID + oldElectrmobile.getElectrmobileId());
        }
        if (null != oldUser) {
            BeanUtils.copyProperties(newUser, oldUser, PropertyUtil.getNullProperties(newUser));
            log.info("修改用户信息:" + JSONObject.toJSONString(newUser, SerializerFeature.DisableCircularReferenceDetect));
            userService.add(oldUser);
            List<Tregistration> result = new ArrayList<>();
            List<Tregistration> registerList = findByUserId(oldUser.getId());
            if(registerList != null && !registerList.isEmpty()){
                for(Tregistration register : registerList){
                    if (StringUtils.isNotEmpty(oldUser.getIdNumber())) {
                        register.setIdNumber(oldUser.getIdNumber());
                    }
                    if (StringUtils.isNotEmpty(oldUser.getName())) {
                        register.setUsername(oldUser.getName());
                    }
                    result.add(register);
                }
                saveAll(result);
            }
            redisUtil.del(CommonConstant.USER_ID + oldUser.getId());
            redisUtil.del(CommonConstant.REGISTER_USERID + oldUser.getId());
        }
        BeanUtils.copyProperties(registration, oldRegistration, PropertyUtil.getNullProperties(registration));
        log.info("修改备案信息:" + JSONObject.toJSONString(oldRegistration, SerializerFeature.DisableCircularReferenceDetect));
        save(oldRegistration);
        deleteRegisterAndElecRedisKey();
        deleteRegisterCache(oldRegistration);
        redisUtil.del(CommonConstant.REGISTER_ID + oldRegistration.getRegisterId());
        TcommonConfig config = commonService.findByName(CommonConstant.LC_BROADCAST_KEY);
        if (config != null && "true".equals(config.getValue())) {
            if (oldElectrmobile != null && !"rfid".equals(oldElectrmobile.getDevtype())) {
                oldRegistration.setElectrombile(oldElectrmobile);
                oldRegistration.setUser(oldUser);
                broadcastUpdateRegister3TdPlatform(oldRegistration);
            }
        }
        return 1;
    }

    @Override
    public Integer changeImei(Long registrationId, String newImei, RegisterQuery query) {
        try {
            log.info("changeImei registration id:" + registrationId);
            Tregistration oldRegistration = validOldRegisterInfo(registrationId);
            String oldImei = oldRegistration.getImei();
            redisUtil.del(CommonConstant.ELEC_ID + oldRegistration.getElectrmobileId());
            redisUtil.del(CommonConstant.REGISTER_ID + registrationId);
            Telectrmobile oldElectrombile = electrombileService.findByElecId(oldRegistration.getElectrmobileId());
            if (!newImei.equals(oldImei)) {
                TdeviceInventory newDevice = deviceInventoryService.findByImeiAndInstallSiteIdIsNotNull(newImei);
                validNewDeviceInventory(newImei, oldImei, newDevice);
                TdeviceInventory deviceInventory = updateNewDeviceInventory(newImei);
                updateOldDeviceInventory(oldImei);
                deleteOldImeiData(oldImei);
                updateElecmobileData(newImei, oldElectrombile, deviceInventory);
                electrombileService.save(oldElectrombile);
                oldRegistration.setImei(newImei);
                oldRegistration.setIsp(deviceInventory.getIsp());
                if (!Objects.equals(oldRegistration.getOperatorId(), deviceInventory.getOperatorId())) {
                    Toperator operator = operatorService.findById(deviceInventory.getOperatorId());
                    if (operator == null) {
                        log.error("device has no operator.imei:" + newImei);
                        throw new NbiotException(600001, OperatorExceptionEnum.E_0002.getMessage());
                    }
                    oldRegistration.setOperatorId(operator.getId());
                }
                if (!Objects.equals(oldRegistration.getInstallSiteId(), deviceInventory.getInstallSiteId())) {
                    TinstallSite site = installSiteService.findById(deviceInventory.getInstallSiteId());
                    if (site == null) {
                        log.error("device has no site.imei:" + newImei);
                        throw new NbiotException(700003, SiteExceptionEnum.E_0005.getMessage());
                    }
                    oldRegistration.setInstallSiteId(site.getInstallSiteId());
                    oldRegistration.setInstallSiteName(site.getName());
                }
                save(oldRegistration);
                saveUpdateRegisterLog(oldRegistration, newImei, oldImei, oldElectrombile.getPlateNumber(), newDevice.getOperatorId());
                int i = nbiotDeviceInfoService.deleteByImei(oldImei);
                log.info("delete nbiot device info success." + i);
                saveDeviceInfo(newDevice);
                deleteRegisterAndElecRedisKey();
                deleteReidsImei(oldImei);
                deleteReidsImei(newImei);
                if(!"kfyd".equals(active)){
                    sendRedisInfo(newDevice, newImei);
                }
                TcommonConfig config = commonService.findByName(CommonConstant.LC_BROADCAST_KEY);
                if (config != null && "true".equals(config.getValue())) {
                    if (!"rfid".equals(newDevice.getDevtype())) {
                        broadcastchangeImei3TdPlatform(registrationId, newImei);
                    }
                }
                return 1;
            } else {
                log.info("neiImei:{},oldImei:{}", newImei, oldImei);
                return 0;
            }
        } catch (Exception e) {
            log.error("e", e);
            throw new NbiotException(10000014, "更换设备失败");
        }
    }

    @Override
    public void deleteRegistration(Long id, String imei, Boolean isDeleteUser) {
        log.info("delete id:" + id + " ,imei:" + imei);
        String userPhone = null;
        Tregistration registration = findById(id);
        if (registration != null) {
            if (isDeleteUser) {
                userPhone = registration.getPhone();
                if (StringUtils.isEmpty(userPhone)) {
                    log.error("userPhone is not exist,register id is:" + id);
                    throw new NbiotException(200002, "用户不存在");
                }
                String userId = registration.getUserId();
                if (countByUserId(userId) == 1) {
                    log.info("delete user id is:" + userId);
                    userService.delete(userId);
                }
            }
            Telectrmobile electrombile = electrombileService.findByElecId(registration.getElectrmobileId());
            if (electrombile != null) {
                redisUtil.del(CommonConstant.ELEC_ID + electrombile.getElectrmobileId());
                redisUtil.del(CommonConstant.ELEC_PLATENUMBER + electrombile.getPlateNumber());
                int i = elecmobileUserService.deleteByElecId(electrombile.getElectrmobileId());
                log.info("delete elec-user success,count is:" + i + ",elecId is:" + electrombile.getElectrmobileId());
            }
            if (StringUtils.isNotEmpty(imei)) {
                TdeviceInventory device = deviceInventoryService.findByImei(imei);
                if (device != null) {
                    deleteRedisData(device);
                }
                deviceInventoryService.updateDevStat(imei, 2);
                electrombileService.deleteByImei(imei);
                int i = nbiotDeviceInfoService.deleteByImei(imei);
                log.info("delete nbiotDeviceInfo, imei:{},i:{}", imei, i);
                deleteOldImeiData(imei);
                deletePolicyInfo(imei);
            }
            deleteRegisterAndElecRedisKey();
            delete(registration, electrombile, null);
            redisUtil.del(CommonConstant.REGISTER_ID + id);
            redisUtil.del(CommonConstant.REGISTER_IMEI + imei);
            redisUtil.del(CommonConstant.REGISTER_USERID + registration.getUserId());
            redisUtil.del(CommonConstant.DEVICE_IMEI + imei);
            TcommonConfig config = commonService.findByName(CommonConstant.LC_BROADCAST_KEY);
            if (config != null && "true".equals(config.getValue())) {
                TdeviceInventory device = deviceInventoryService.findByImei(imei);
                if (!"rfid".equals(device.getDevtype()) || !CommonConstant.DEVICE_MODE_310.equals(device.getModelNo())) {
                    broadCastDeleteRegister3TdPlatform(id);
                }
            }
        }
    }

    @Override
    public Integer deleteRegister(Long id) {
        Tregistration register = findById(id);
        if(register == null){
            throw new NbiotException(10000007, "该备案信息不存在");
        }
        if(StringUtils.isNotEmpty(register.getImei())){
            deleteRegistration(id, register.getImei(), false);
        }
        else{
            deleteRegistration(id, null, false);
        }
        return 1;
    }

    @Override
    public Tregistration delete(Tregistration registration, Telectrmobile electrmobile, Tuser user) {
//        Tregistration register = findById(id);
        if (registration != null) {
            log.info("delete registerId is:{}",registration.getRegisterId());
            registerRepository.deleteByRegisterId(registration.getRegisterId());
            registration.setElectrombile(electrmobile);
            registration.setUser(user);
            saveDeleteRegisterLog(registration);
            log.info("delete register success, registerId is:" + registration.getRegisterId());
        }
        return null;
    }

    @Override
    public Integer deleteByElecId(Long elecId) {
        return registerRepository.deleteByElecId(elecId);
    }

    @Override
    public Tregistration addRegistration(Tregistration data) {
        String imei = data.getElectrombile().getImei();
        TdeviceInventory deviceInventory = deviceInventoryService.findByImei(imei);
        validDeviceInfo(deviceInventory);
        //添加电动车
        String userId = data.getUser().getId();
        if (StringUtils.isNotEmpty(userId)) {
            Tuser user = userService.findById(userId);
            if(user == null){
                log.error("use not found.id:" + userId);
                throw new NbiotException(200002, "用户不存在");
            }
            Tuser tuser = userService.updateUser(data.getUser());
            Tregistration register = register(data, false);
            return register;
        }
        //添加user及电动车
        else if (StringUtils.isEmpty(userId)) {
            return register(data, true);
        }
        log.error("wrong.data:" + JSONObject.toJSONString(data));
        throw new NbiotException(400, "");
    }


    @Override
    public Long countByQuery(RegisterQuery registerQuery) {
        Long count = registerRepository.count(new Specification<Tregistration>() {
            private static final long serialVersionUID = 5607720374819010785L;
            @Override
            public Predicate toPredicate(Root<Tregistration> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (registerQuery != null) {
                    log.debug("");
                    if (registerQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(registerQuery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return count;
    }
    
    @Override
    public Tregistration save(Tregistration registration) {
        Tregistration entity = registerRepository.save(registration);
        redisUtil.del(CommonConstant.REGISTER_ID + registration.getRegisterId());
        log.info("save register success.id:" + entity.getRegisterId());
        return entity;
    }

    @Override
    public Tregistration findByImei(String imei) {
        String registerImeiString = (String) redisUtil.get(CommonConstant.REGISTER_IMEI + imei);
        if (StringUtils.isEmpty(registerImeiString)) {
            Tregistration registration = registerRepository.findByImei(imei);
            if (registration != null) {
                redisUtil.set(CommonConstant.REGISTER_IMEI + imei, JSON.toJSONString(registration));
            }
            return registration;
        }
        log.info("get register data from redis.imei:" + imei);
        return JSONObject.parseObject(registerImeiString, Tregistration.class);
//        Tregistration registration = registerRepository.findByImei(imei);
//        return registration;
    }

    @Override
    public List<Tregistration> findByUserId(String userId) {
        String registerString = (String) redisUtil.get(CommonConstant.REGISTER_USERID + userId);
        ListTranscoder<Tregistration> listTranscoder = new ListTranscoder<Tregistration>();
        if (StringUtils.isEmpty(registerString)) {
            List<Tregistration> registrations = registerRepository.findByUserId(userId);
            if (registrations != null && !registrations.isEmpty()) {
                redisUtil.set(CommonConstant.REGISTER_USERID + userId, listTranscoder.serialize(registrations));
            }
            return registrations;
        }
        log.info("get register data from redis.userId:" + userId);
        Object o = listTranscoder.deserialize(registerString);
        return (List<Tregistration>) o;
//        return registerRepository.findByUserId(userId);
    }

    @Override
    public void exportExcel(HttpServletResponse response, RegisterQuery query) {
        List<TregisterView> list = findAllByDate(query);
        ExportExcelData data = addExcelData(list);
        try {
            String excelName = TimeStampUtil.getTs(query.getStartTime()) + "至" + TimeStampUtil.getTs(query.getEndTime()) + "安装统计.xls";
            ExportExcelUtils.exportExcel(response, excelName, data);
        } catch (Exception e) {
            log.error("e", e);
            throw new NbiotException(500, "导出失败");
        }
    }

    @Override
    public Tregistration findByIotDeviceId(String deviceId) {
        String registrationString = (String) redisUtil.get(CommonConstant.REGISTER_IOTDEVICEID + deviceId);
        if (StringUtils.isEmpty(registrationString)) {
            log.debug("registerDeviceId:" + deviceId);
            TdeviceInventory deviceInventory = deviceInventoryService.findByIotDeviceId(deviceId);
            if (deviceInventory != null) {
                Tregistration registration = findByImei(deviceInventory.getImei());
                redisUtil.set(CommonConstant.REGISTER_IOTDEVICEID + deviceId, JSON.toJSONString(registration), 10, TimeUnit.DAYS);
                return registration;
            }
            log.error("registerDeviceId:" + deviceId + " not exist");
            return null;
        }
        Tregistration registration = JSONObject.parseObject(registrationString, Tregistration.class);
        return registration;
    }

    @Override
    public Tregistration findById(Long id) {
        String registerString = (String) redisUtil.get(CommonConstant.REGISTER_ID + id);
        if (StringUtils.isEmpty(registerString)) {
            Tregistration registration = registerRepository.findByRegisterId(id);
            if (registration != null) {
                redisUtil.set(CommonConstant.REGISTER_ID + id, JSON.toJSONString(registration), 1, TimeUnit.DAYS);
            }
            return registration;
        }
        log.info("get register data from redis.id:" + id);
        Tregistration registration = JSONObject.parseObject(registerString, Tregistration.class);
        return registration;
    }

    @Override
    public Tregistration findByPlateNo(String plateNo) {
        return registerRepository.findByPlateNo(plateNo);
    }

    @Override
    public Integer countByUserId(String userId) {
        return registerRepository.countByUserId(userId);
    }

    @Override
    public void deleteRegisterRedis() {
        if (redisUtil.keys(CommonConstant.REGISER_PATTERN) != null && redisUtil.keys(CommonConstant.REGISER_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.REGISER_PATTERN);
            for (String key : sets) {
                log.info("del redis key [" + key + "]");
                redisUtil.del(key);
            }
        }
    }

    @Override
    public Integer deleteByImei(String imei) {
        return registerRepository.deleteByImei(imei);
    }

    @Override
    public Tregistration getInfoById(Long id) {
        Tregistration register = findById(id);
        if (getRegisterInfo(register)) return register;
        return null;
    }

    @Override
    public Tregistration getInfoByPhone(String phone) {
        Tregistration register = registerRepository.findByPhone(phone);
        if (getRegisterInfo(register)) return register;
        return null;
    }

    @Override
    public List<Tregistration> findByUserIdAndOperatorIdsAndType(String userId, List<Integer> operatorIds, String type) {
        return registerRepository.findByUserIdAndOperatorIdsAndType(userId, operatorIds, type);
    }

    @Override
    public List<String> findByUserNameLikeAndImeiIsNotNUll(String ownerName) {
        return registerRepository.findByUserNameLikeAndImeiIsNotNUll(ownerName);
    }

    @Override
    public List<String> findByUserIdAndImeiIsNotNUll(String ownerId) {
        return registerRepository.findByUserIdAndImeiIsNotNull(ownerId);
    }

    private void deletePolicyInfo(String imei) {
        TpolicyInfo policyInfo = policyInfoService.findByImei(imei);
        if(policyInfo != null && !policyInfo.getIsEffective() && StringUtils.isEmpty(policyInfo.getPlateNo())){
            policyInfo.setUserId(null);
            policyInfoService.save(policyInfo);
        }
    }

    private void saveAll(List<Tregistration> result) {
        registerRepository.saveAll(result);
    }

    /**
     * 添加销售记录
     */
    public void addSaleRecord(TdeviceInventory device) {
        TsaleRecord saleRecord = saleRecordService.findByImeiAndMode(device.getImei(), 2);
        if (saleRecord == null) {
            saleRecord = new TsaleRecord();
            saleRecord.setImei(device.getImei());
            saleRecord.setMode(2);
            saleRecord.setOperatorId(device.getOperatorId());
            saleRecord.setStorehouseId(device.getStorehouseId());
            saleRecordService.add(saleRecord);
        }
        else {
            saleRecord.setUpdateTime(new Date());
            saleRecordService.add(saleRecord);
        }
    }

    private boolean getRegisterInfo(Tregistration register) {
        if (register != null) {
            Telectrmobile electrmobile = electrombileService.findByElecId(register.getElectrmobileId());
            Tuser user = userService.findById(register.getUserId());
            register.setElectrombile(electrmobile);
            register.setUser(user);
            return true;
        }
        return false;
    }

    /**
     * 添加设备状态
     */
    private void saveDeviceState(String imei, TdeviceInventory deviceInventory) {
        TdeviceState deviceState = new TdeviceState();
        deviceState.setImei(imei);
        deviceState.setFwVersion(deviceInventory.getSwVersion());
        deviceState.setUint(86400);
        deviceState.setStateTime(new Date().getTime());
        deviceState.setOtherTime(new Date().getTime());
        deviceStateService.save(deviceState);
    }

    /**
     * 往redis中保存指定key信息
     */
    public void sendRedisInfo(TdeviceInventory deviceInventory, String imei) {
        boolean success = false;
        boolean imeiSuccess = false;
        if (StringUtils.isNotEmpty(deviceInventory.getIotDeviceId())) {
            success = secondRedisUtil.set(CommonConstant.REGISTER_KEY + deviceInventory.getIotDeviceId(), imei);
            imeiSuccess = secondRedisUtil.set(CommonConstant.REGISTER_KEY + imei, imei);
        } else {
            success = secondRedisUtil.set(CommonConstant.REGISTER_KEY + imei, imei);
        }
        boolean operatorResult = redisUtil.set(CommonConstant.OPERATOR_IMEI_KEY + imei, String.valueOf(deviceInventory.getOperatorId()));
        log.info("save redis imei:" + imei + ",register iotDeviceId result:" + success + ",imeiSuccess:" + imeiSuccess + ",operator result:" + operatorResult);
    }

    /**
     * 创建电动车
     */
    public Telectrmobile createElecmobile(Telectrmobile electrombile, TdeviceInventory device, Tuser newUser, Toperator operator) {
        setElecValue(electrombile, device, newUser, installSiteService, operator);
        Telectrmobile newElectrombile = electrombileService.createElectrombile(electrombile);
        redisUtil.del(CommonConstant.ELEC_ID + newElectrombile.getElectrmobileId());
        return newElectrombile;
    }

    public static void setElecValue(Telectrmobile electrombile, TdeviceInventory deviceInventory, Tuser newUser,
                             InstallSiteService installSiteService, Toperator workerOperator) {
        if (deviceInventory != null && deviceInventory.getStorehouseId() != null) {
            electrombile.setStorehouseId(deviceInventory.getStorehouseId());
            electrombile.setStorehouseName(deviceInventory.getStorehouseName());
        }
        if (deviceInventory != null && deviceInventory.getRegionId() != null) {
            electrombile.setRegionId(deviceInventory.getRegionId());
            electrombile.setRegionName(deviceInventory.getRegionName());
        }
        if (deviceInventory != null && deviceInventory.getManufactorId() != null) {
            electrombile.setManufactorId(deviceInventory.getManufactorId());
            electrombile.setManufactorName(deviceInventory.getManufactorName());
        }
        if (deviceInventory != null && deviceInventory.getInstallSiteId() != null) {
            electrombile.setInstallSiteId(deviceInventory.getInstallSiteId());
            electrombile.setInstallSiteName(deviceInventory.getInstallSiteName());
            TinstallSite site = installSiteService.findById(deviceInventory.getInstallSiteId());
            if (site != null) {
                electrombile.setPoliceId(site.getPoliceId());
                electrombile.setPoliceName(site.getPoliceName());
            }
        }
        if (deviceInventory != null && deviceInventory.getIotTypeId() != null) {
            electrombile.setIotTypeId(deviceInventory.getIotTypeId());
            electrombile.setIotTypeName(deviceInventory.getIotTypeName());
        }
        if (deviceInventory != null && deviceInventory.getOperatorId() != null) {
            electrombile.setOperatorId(deviceInventory.getOperatorId());
            electrombile.setOperatorName(deviceInventory.getOperatorName());
        }
        else if(workerOperator != null){
            electrombile.setOperatorId(workerOperator.getId());
            electrombile.setOperatorName(workerOperator.getName());
        }
        if(deviceInventory != null && StringUtils.isNotEmpty(deviceInventory.getDevname())){
            electrombile.setDevname(deviceInventory.getDevname());
        }
        if (newUser != null) {
            electrombile.setOwnerId(newUser.getId());
            electrombile.setOwnerName(newUser.getName());
        }
        if(deviceInventory != null && StringUtils.isNotEmpty(deviceInventory.getModelNo())){
            electrombile.setModelNo(deviceInventory.getModelNo());
        }
        if(deviceInventory != null && StringUtils.isNotEmpty(deviceInventory.getDevtype())){
            electrombile.setDevtype(deviceInventory.getDevtype());
        }
    }

    /**
     * 修改电动车数据
     */
    private void updateElecmobileData(String newImei, Telectrmobile oldElectrombile, TdeviceInventory device) {
        oldElectrombile.setImei(newImei);
        if (device.getStorehouseId() != null) {
            oldElectrombile.setStorehouseId(device.getStorehouseId());
            oldElectrombile.setStorehouseName(device.getStorehouseName());
        } else {
            oldElectrombile.setStorehouseId(null);
            oldElectrombile.setStorehouseName(null);
        }
        if (device.getRegionId() != null) {
            oldElectrombile.setRegionId(device.getRegionId());
            oldElectrombile.setRegionName(device.getRegionName());
        } else {
            oldElectrombile.setRegionId(null);
            oldElectrombile.setRegionName(null);
        }
        if (device.getManufactorId() != null) {
            oldElectrombile.setManufactorId(device.getManufactorId());
            oldElectrombile.setManufactorName(device.getManufactorName());
        } else {
            oldElectrombile.setManufactorId(null);
            oldElectrombile.setManufactorName(null);
        }
        if (device.getInstallSiteId() != null && !Objects.equals(device.getInstallSiteId(), oldElectrombile.getInstallSiteId())) {
            oldElectrombile.setInstallSiteId(device.getInstallSiteId());
            oldElectrombile.setInstallSiteName(device.getInstallSiteName());
            TinstallSite site = installSiteService.findById(device.getInstallSiteId());
            if (site != null && site.getPoliceId() != null) {
                TpolicePrecinct police = policePrecinctService.findById(site.getPoliceId());
                if(police != null){
                    oldElectrombile.setPoliceId(police.getId());
                    oldElectrombile.setPoliceName(police.getPoliceStation());
                }
            } else {
                oldElectrombile.setPoliceId(null);
                oldElectrombile.setPoliceName(null);
            }
        }
        if (device.getIotTypeId() != null) {
            oldElectrombile.setIotTypeId(device.getIotTypeId());
        } else {
            oldElectrombile.setIotTypeId(null);
        }
        if(!Objects.equals(oldElectrombile.getDevname(), device.getDevname())){
            oldElectrombile.setDevname(device.getDevname());
        }
        if(!Objects.equals(oldElectrombile.getDevtype(), device.getDevtype())){
            oldElectrombile.setDevtype(device.getDevtype());
        }
        if(!Objects.equals(oldElectrombile.getModelNo(), device.getModelNo())){
            oldElectrombile.setModelNo(device.getModelNo());
        }
        if(!Objects.equals(oldElectrombile.getOperatorId(), device.getOperatorId())){
            oldElectrombile.setOperatorId(device.getOperatorId());
            Toperator operator = operatorService.findById(device.getOperatorId());
            if(operator != null){
                oldElectrombile.setOperatorId(operator.getId());
                oldElectrombile.setOperatorName(operator.getName());
            }else {
                oldElectrombile.setOperatorId(null);
                oldElectrombile.setOperatorName(null);
            }
        }
    }

    /**
     * 删除旧的imei数据
     */
    public void deleteOldImeiData(String oldImei) {
        log.info("start delete old imei data,old imei is:" + oldImei);
        nbiotDeviceAlarmRtDataService.deleteByImei(oldImei);
        nbiotDeviceRtDataService.deleteByImei(oldImei);
        nbiotTrackerDataService.deleteByImei(oldImei);
        nbiotHistoryWlanDataService.deleteByImei(oldImei);
        esTrackvelService.deleteByImei(oldImei);
        esNbiotDeviceAlarmService.deleteByImei(oldImei);
        deviceStateService.deleteByImei(oldImei);
        deleteRedisByImei(oldImei);
        try {
            Result result = dotService.deleteDot(oldImei);
            log.info("delete dot result:" + result);
        } catch (Exception e) {
            log.error("e", e);
        }
        log.info("delete old imei data finished,old imei is:" + oldImei);
    }

    /**
     * 修改旧的deviceInventory的Devstate
     */
    private void updateOldDeviceInventory(String oldImei) {
        TdeviceInventory oldDevice = deviceInventoryService.findByImei(oldImei);
        if (oldDevice != null) {
            oldDevice.setDevstate(2);
            deviceInventoryService.save(oldDevice);
//            redisUtil.del(CommonConstant.DEVICE_IMEI + oldImei);
//            deviceInventoryService.deleteRedisDeviceImeiByImei(oldImei);
            log.info("update old device devstate success, oldImei is:" + oldImei);
        } else {
            log.error("old device is not exist,imei is:" + oldImei);
            throw new NbiotException(10000001, "设备不存在");
        }
    }

    /**
     * 修改新的IMEI的dev_stat状态
     */
    private TdeviceInventory updateNewDeviceInventory(String newImei) {
        TdeviceInventory newDevice = deviceInventoryService.findByImei(newImei);
        newDevice.setDevstate(1);
        TdeviceInventory inventory = deviceInventoryService.save(newDevice);
        log.info("update new device devstate success, imei is :" + newImei);
        return inventory;
    }

    /**
     * 保存新增备案
     */
    private Tregistration saveRegister(Telectrmobile electrombile, Tuser user, TdeviceInventory device, String payNumber) {
        Tregistration registration = new Tregistration();
        if(device != null){
            registration.setImei(device.getImei());
            registration.setOperatorId(device.getOperatorId());
            registration.setInstallSiteId(device.getInstallSiteId());
            registration.setInstallSiteName(device.getInstallSiteName());
            registration.setIsp(device.getIsp());
            registration.setModelNo(device.getModelNo());
        }else{
            registration.setOperatorId(electrombile.getOperatorId());
        }
        registration.setPayNumber(payNumber);
        registration.setElectrmobileId(electrombile.getElectrmobileId());
        registration.setPlateNumber(electrombile.getPlateNumber());
        registration.setUserId(user.getId());
        registration.setIdNumber(user.getIdNumber());
        registration.setPhone(user.getPhone());
        registration.setUsername(user.getName());
        Tregistration tregistration = save(registration);
        if (tregistration == null) {
            log.error("新增备案信息失败：" + registration.toString());
            throw new NbiotException(10000003, "新增备案信息失败");
        }
        tregistration.setUser(user);
        tregistration.setElectrombile(electrombile);
        return tregistration;
    }

    /**
     * 保存电动车、用户关系
     */
    private void createElecUserRelationShip(String userId, Long electrombileId, Integer operatorId) {
        TelectrombileUser electrombileUser = new TelectrombileUser();
        electrombileUser.setUserId(userId);
        electrombileUser.setElectrombileId(electrombileId);
        electrombileUser.setOperatorId(operatorId);
        TelectrombileUser entity = elecmobileUserService.save(electrombileUser);
        log.info("save electrmobile-User success.id is:" + entity.getId());
    }

    /**
     * 验证旧的备案信息
     */
    private Tregistration validOldRegisterInfo(Long registrationId) {
        Tregistration oldRegistration = registerRepository.findByRegisterId(registrationId);
        if (oldRegistration == null) {
            throw new NbiotException(10000007, "该备案信息不存在");
        }
        if (StringUtils.isEmpty(oldRegistration.getImei())) {
            log.error("imei is null,registerId is :" + registrationId);
            throw new NbiotException(10000009, "该备案信息有误");
        }
        return oldRegistration;
    }

    /**
     * 修改备案时参数校验
     */
    private void validUpdateRegisterParameter(Telectrmobile oldElectrombile, Telectrmobile newElectrombile) {
        if (newElectrombile == null) {
            log.error("electrombile is null.");
            throw new NbiotException(400, "参数错误");
        }
        if (!oldElectrombile.getVin().equals(newElectrombile.getVin())) {
            validElecmobileVin(newElectrombile.getVin());
        }
        if (!oldElectrombile.getPlateNumber().equals(newElectrombile.getPlateNumber())) {
            validElecPlateNo(newElectrombile.getPlateNumber());
        }
    }

    /**
     * 验证新的设备是否符合要求
     */
    private void validNewDeviceInventory(String newImei, String oldImei, TdeviceInventory newDevice) {
        if (null == newDevice) {
            log.error("设备未注册：" + newImei);
            //RegisterExceptionEnum.E_0002
            throw new NbiotException(10000001, "设备未注册");
        }
//		TdeviceInventory oldDeviceInventory = deviceInventoryService.findByImei(oldImei);
//		if(oldDeviceInventory.getInstallSite() != null && newDeviceInventory.getInstallSite() != null) {
//			if(!Objects.equals(oldDeviceInventory.getInstallSite().getInstallSiteId(), newDeviceInventory.getInstallSite().getInstallSiteId())) {
//				logger.error("installSiteId is not equils,oldImei:{},newImei:{}", oldImei, newImei);
//				throw new NbiotException(DeviceExceptionEnum.E_0008.getCode(), DeviceExceptionEnum.E_0008.getMessage());
//			}
//		}
        log.info("device change...,new imei is:" + newImei + ",old imei is:" + oldImei);
        if (Objects.equals(1, newDevice.getDevstate())) {
            log.error("the device has installed. imei:{}", newImei);
            //RegisterExceptionEnum.E_00010
            throw new NbiotException(10000005, "该设备已安装");
        }
        //华强仓库下
        if(Objects.equals(1, newDevice.getStorehouseId())){
            log.error("the device storehouse in HQ. imei:{}", newImei);
            //RegisterExceptionEnum.E_0007
            throw new NbiotException(10000004, "未入库、已入库未分配的设备不可以注册");
        }
    }
    /**
     * 验证车牌号是否存在
     */
    public void validElecPlateNo(String plateNo) {
        Telectrmobile elec = electrombileService.findByPlateNumber(plateNo);
        if (elec != null) {
            log.error("plateno exist! plateNO:" + plateNo);
            throw new NbiotException(3000003, "电动车车牌已存在");
        }
    }
    /**
     * 判断电动车车架号是否为空且不存在
     */
    public void validElecmobileVin(String vin) {
        if (StringUtils.isEmpty(vin)) {
            throw new NbiotException(3000010, "车架号不能为空");
        }
        List<Telectrmobile> electrombiles = electrombileService.findByVin(vin);
        if (electrombiles != null && electrombiles.size() > 0) {
            log.error("vin exist.vin:" + vin);
            throw new NbiotException(3000009, "车架号已存在");
        }
    }

    /**
     * 验证用户信息
     */
    private Tuser validUser(String userId) {
        Tuser user = userService.findById(userId);
        if (user == null) {
            log.error("valid user wrong.user is not exist. userId:" + userId);
            throw new NbiotException(200002, "用户不存在");
        }
        return user;
    }

    private void validUserPhone(String phone, Boolean valid) {
        if (StringUtils.isNotEmpty(phone) && valid) {
            Tuser tuser = userService.findByPhone(phone);
            if (tuser != null) {
                log.error("phone has exist. phone:" + phone);
                throw new NbiotException(10000012, "登录账号已存在");
            }
        }
        else if(valid){
            throw new NbiotException(10000013, "登录账号不能为空");
        }
    }

    /**
     * 创建user
     */
    private Tuser createUser(TdeviceInventory deviceInventory, Tuser tuser) {
        String userId = StringUtil.createUUID();
        tuser.setId(userId);
        tuser.setCreateTime(TimeStampUtil.getTs());
        tuser.setPassword(MD5Util.MD5(tuser.getIdNumber().substring(tuser.getIdNumber().length() - 6, tuser.getIdNumber().length())));
        tuser.setLoginName(tuser.getPhone());
        if (StringUtils.isEmpty(tuser.getTenantId())) {
            tuser.setTenantId("0");
        }
        if (StringUtils.isEmpty(tuser.getSystemId())) {
            tuser.setSystemId("6");
        }
        if(deviceInventory != null){
            tuser.setDeviceStorehouseId(deviceInventory.getStorehouseId());
            tuser.setInstallSiteId(deviceInventory.getInstallSiteId());
            tuser.setIotTypeId(deviceInventory.getIotTypeId());
            tuser.setOperatorId(deviceInventory.getOperatorId());
            tuser.setResidentId(deviceInventory.getRegionId());
        }
        log.info("create user is:" + JSONObject.toJSONString(tuser));
        userService.insertUserRoleR(StringUtil.createUUID(), userId, roleRepository.getRoleId());
        return userService.add(tuser);
    }

    /**
     * 保存更换设备registerLog
     */
    private void saveUpdateRegisterLog(Tregistration registration, String newImei, String oldImei, String plateNo, Integer operatorId) {
        TregistrationLog logg = new TregistrationLog();
        logg.setRegisterId(registration.getRegisterId());
        logg.setOldImei(oldImei);
        logg.setNewImei(newImei);
        logg.setType(1);
        logg.setOldPlateNo(plateNo);
        logg.setOperatorId(operatorId);
        registrationLogService.save(logg);
        log.info("save register log success,newImei is {}", newImei);
    }

    /**
     * 保存删除的register记录
     */
    public void saveDeleteRegisterLog(Tregistration registration) {
        TregistrationLog log = new TregistrationLog();
        log.setRegisterId(registration.getRegisterId());
        log.setType(3);
        log.setOldPlateNo(registration.getPlateNumber());
        log.setOperatorId(registration.getOperatorId());
        log.setOldData(JSONObject.toJSONString(registration));
        log.setUserId(registration.getUserId());
        registrationLogService.save(log);
        registrationLogService.updateByRegisterId(registration.getRegisterId());
    }

    /**
     * 修改DeviceInventory的dev_state状态为已安装
     */
    private void updateDeviceInventoryStatus(String imei) {
        TdeviceInventory device = deviceInventoryService.findByImei(imei);
        if (device != null) {
            device.setDevstate(1);
            deviceInventoryService.save(device);
        }
    }

    /**
     * 保存deviceInfo
     */
    public void saveDeviceInfo(TdeviceInventory device) {
        NbiotDeviceInfo nbiotDeviceInfo = new NbiotDeviceInfo();
        nbiotDeviceInfo.setDeviceId(device.getIotDeviceId());
        nbiotDeviceInfo.setImei(device.getImei());
        if (device.getInstallSiteId() != null) {
            nbiotDeviceInfo.setInstallSiteId(device.getInstallSiteId());
        }
        if (device.getIotTypeId() != null) {
            nbiotDeviceInfo.setIotTypeId(device.getIotTypeId());
        }
        if (device.getOperatorId() != null) {
            nbiotDeviceInfo.setOperatorId(device.getOperatorId());
        }
        if (device.getRegionId() != null) {
            nbiotDeviceInfo.setResidentId(device.getRegionId());
        }
        if (device.getStorehouseId() != null) {
            nbiotDeviceInfo.setDeviceStorehouseId(device.getStorehouseId());
        }
        nbiotDeviceInfo.setStatus((byte) 2);
        log.info("nbiotDeviceInfo:" + JSONObject.toJSONString(nbiotDeviceInfo));
        nbiotDeviceInfo = nbiotDeviceInfoService.add(nbiotDeviceInfo);
        log.info("nbiotDeviceInfo save result:" + nbiotDeviceInfo.getInfoId());
    }

    /**
     * 用户推送
     */
    public void userPush(Tuser newUser) {
        TuserPush userPush = new TuserPush();
        userPush.setChannelId(newUser.getPhone());
        userPush.setSystemId(6);
        userPush.setType(2);
        userPush.setStatus(0);
        userPush.setUserId(newUser.getId());
        userPush.setId(StringUtil.createUUID());
        userPush.setCreateTime(TimeStampUtil.getTs());
        userPush.setUpdateTime(TimeStampUtil.getTs());
        log.info("usePush is:" + JSONObject.toJSONString(userPush));
        userPush = userPushService.add(userPush);
        log.info("save userPush success,id:{},userId:{}", userPush.getId(), newUser.getId());
    }

    /**
     * 验证设备是否符合安装要求
     */
    private void validDeviceInfo(TdeviceInventory deviceInventory) {
        String imei = deviceInventory.getImei();
        if (findByImei(imei) != null) {
            log.error("imei:" + imei + " has registered.");
            //RegisterExceptionEnum.E_00010
            throw new NbiotException(10000005, "");
        }
        if (deviceInventory.getInstallSiteId() == null) {
            log.error("imei:" + imei + " installSite is null.");
            //RegisterExceptionEnum.E_0007
            throw new NbiotException(10000004, "");
        }
        if(Objects.equals(1, deviceInventory.getStorehouseId())){
            log.error("imei:" + imei + " storehouse is HQ.");
            ////RegisterExceptionEnum.E_0007
            throw new NbiotException(10000004, "");
        }
    }

    /**
     * 删除redis中key
     */
    public void deleteRegisterAndElecRedisKey() {
        deleteRedisSet(CommonConstant.REGISER_PATTERN);
        deleteRedisSet(CommonConstant.ELEC_PATTERN);
    }

    /**
     * 删除redis的数据
     */
    public void deleteRedisData(TdeviceInventory device) {
        deleteSecondRedis(CommonConstant.REGISTER_KEY, device.getIotDeviceId(), "register-");
        deleteSecondRedis(CommonConstant.REGISTER_KEY, device.getImei(), "register-");
        deleteSecondRedis(CommonConstant.OPERATOR_IMEI_KEY, device.getImei(), "operator-imei-");
    }

    private void deleteSecondRedis(String key, String imei, String s) {
        if (secondRedisUtil.hasKey(key + imei)) {
            secondRedisUtil.del(key + imei);
            log.info("redis delete key [" + s + imei + "] success");
        }
    }

    public void deleteRedisByImei(String imei) {
        String imeiKey = CommonConstant.REGISTER_IMEI + imei;
        String deviceKey = CommonConstant.DEVSTATE_IMEI + imei;
        String registerDeviceIdKey = CommonConstant.OPERATOR_IMEI_KEY + imei;
        String registerAllInfoKey = CommonConstant.REGISTER_KEY + imei;
        String elecInfoKey = CommonConstant.ELEC_IMEI + imei;
//        String deviceImeiKey = CommonConstant.DEVICE_IMEI + imei;
        String devStateImeiKey = CommonConstant.STATUS_IMEI + imei;
        String rtDataKey = CommonConstant.RT_DATA_KEY + imei;
        String rtDataImeiKey = CommonConstant.RTDATA_IMEI_KEY + imei;
        String rtWlanDataKey = CommonConstant.RT_WLANDATA_IMEI_KEY + imei;
        deleteRedisByKey(imeiKey);
        deleteRedisByKey(deviceKey);
        deleteRedisByKey(registerDeviceIdKey);
        deleteRedisByKey(registerAllInfoKey);
        deleteRedisByKey(elecInfoKey);
//        deleteRedisByKey(deviceImeiKey);
//        deleteRedisDeviceImeiByImei(imei);
        deleteRedisByKey(devStateImeiKey);
        deleteRedisByKey(rtDataKey);
        deleteRedisByKey(rtDataImeiKey);
        deleteRedisByKey(rtWlanDataKey);
        deleteReidsImei(imei);
    }

    private void deleteRegisterCache(Tregistration oldRegistration) {
       String imei = oldRegistration.getImei();
        deleteRedisByKey(CommonConstant.ELEC_IMEI + imei);
        deleteRedisByKey(CommonConstant.REGISTER_IMEI + imei);
    }

    /**
     * 删除该设备在redis中的临时数据（gps）
     */
    private void deleteReidsImei(String imei) {
        String imeiKey = CommonConstant.IEMP_NBIOT_TRACKER_IMEI + imei + "*";
        deleteRedisSet(imeiKey);
        deleteRedisByKey(imeiKey);
    }

    private void deleteRedisSet(String imeiKey) {
        if (redisUtil.keys(imeiKey) != null && redisUtil.keys(imeiKey).size() > 0) {
            log.info("keys [" + imeiKey + "] exist......");
            Set<String> sets = redisUtil.keys(imeiKey);
            for (String key : sets) {
                redisUtil.del(key);
            }
        }
    }

    private void deleteRedisByKey(String deviceKey) {
        if (redisUtil.hasKey(deviceKey)) {
            redisUtil.del(deviceKey);
            log.info("redis delete key [" + deviceKey + "] success");
        }
    }

//    private void deleteRedisDeviceImeiByImei(String imei) {
//        if (redisUtil.hHasKey(CommonConstant.DEVICE_IMEI, imei)) {
//            redisUtil.hdel(CommonConstant.DEVICE_IMEI, imei);
//            log.info("redis delete key [" + CommonConstant.DEVICE_IMEI + imei + "] success");
//        }
//    }

    /**
     * 填充excel数据
     */
    private ExportExcelData addExcelData(List<TregisterView> list) {
        ExportExcelData data = new ExportExcelData();
        data.setName("安装统计");
        addExcelTitle(data);
        List<List<String>> rows = new LinkedList<>();
        if (list != null && list.size() > 0) {
            addExcelCellData(list, rows);
            log.info("export list size is:" + list.size());
            data.setRows(rows);
        }
        return data;
    }

    /**
     * 添加数据
     */
    private void addExcelCellData(List<TregisterView> list, List<List<String>> rows) {
        for (TregisterView view : list) {
            List<String> row = new LinkedList<>();
            row.add(view.getInstallSiteName());
            row.add(view.getImei());
            row.add(view.getPolicyNo());
            row.add(view.getPlateNumber());
            row.add(view.getUsername());
            row.add(view.getBirthPlace());
//            row.add(view.getContactPhone());
            row.add(view.getIdNumber());
            if (view.getCreateTime() != null) {
                row.add(view.getCreateTime().toString());
            } else {
                row.add("");
            }
            row.add(view.getPhone());
            row.add(view.getElecType());
            row.add(view.getElecVendor());
            row.add(view.getElecColor());
            row.add(view.getVin());
            if (view.getPurchaseTime() != null) {
                row.add(view.getPurchaseTime().toString());
            } else {
                row.add("");
            }
            row.add(view.getPersonInCharge());
            row.add(view.getInstallSitePhone());
            row.add(view.getPoliceStation());
            row.add(view.getOnlineStatus());
            row.add(view.getIsp());
            rows.add(row);
        }
    }

    private List<TregisterView> findAllByDate(RegisterQuery aepQuery) {
        if(aepQuery == null){
            return null;
        }
        List<Map<String, Object>> lists = null;
        if (aepQuery.getOperatorIdList() != null) {
            lists = registerRepository.exportAllByTime(aepQuery.getStartTime(), aepQuery.getEndTime(), aepQuery.getOperatorIdList());
        } else {
            lists = registerRepository.exportAllByTime(aepQuery.getStartTime(), aepQuery.getEndTime());
        }
        List<TregisterView> list = new ArrayList<>();
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                TregisterView view = new TregisterView();
                Date createTime = (Date) map.get("create_time");
                String plateNumber = (String) map.get("plate_number");
                String imei = (String) map.get("imei");
                String policyNo = (String) map.get("policy_no");
                String username = (String) map.get("username");
                String phone = (String) map.get("phone");
                String idNumber = (String) map.get("id_number");
                String birthplace = (String) map.get("birthplace");
                String contactPhone = (String) map.get("contact_phone");
                String type = (String) map.get("type");
                String vendor = (String) map.get("vendor");
                String color = (String) map.get("color");
                String vin = (String) map.get("vin");
                Date purchaseTime = (Date) map.get("purchase_time");
                String installSiteName = (String) map.get("install_site_name");
                String personCharge = (String) map.get("person_in_charge");
                String installSitePhone = (String) map.get("install_site_phone");
                String policeStationName = (String) map.get("police_station_name");
                Integer devstate = (Integer) map.get("devstate");
                String isp = (String) map.get("isp");
                view.setBirthPlace(birthplace);
                view.setContactPhone(contactPhone);
                view.setCreateTime(createTime);
                view.setElecColor(color);
                view.setElecType(type);
                view.setElecVendor(vendor);
                view.setIdNumber(idNumber);
                view.setImei(imei);
                view.setPolicyNo(policyNo);
                view.setInstallSiteName(installSiteName);
                view.setInstallSitePhone(installSitePhone);
                view.setIsp(isp);
                if(Objects.equals(1, devstate)){
                    view.setOnlineStatus("已安装");
                }else if(Objects.equals(2, devstate)){
                    view.setOnlineStatus("更换");
                }else{
                    view.setOnlineStatus("未安装");
                }
                view.setPersonInCharge(personCharge);
                view.setPhone(phone);
                view.setPlateNumber(plateNumber);
                view.setPoliceStation(policeStationName);
                view.setPurchaseTime(purchaseTime);
                view.setUsername(username);
                view.setVin(vin);
                list.add(view);
            }
        }
        return list;
    }

    /**
     * 通知第三方平台注册备案
     */
    private void broadcastRegister3TdPlatform(Tregistration registration) {
        ResponseEntity<JSONObject> response = null;
        try {
            JSONObject body = new JSONObject();
            JSONObject user = new JSONObject();
            JSONObject elec = new JSONObject();
            Tuser tuser = registration.getUser();
            Telectrmobile telectrmobile = registration.getElectrombile();
            user.put("name", tuser.getName());
            user.put("contactPhone", tuser.getContactPhone());
            user.put("phone", tuser.getPhone());
            user.put("home", tuser.getHome());
            user.put("idType", tuser.getIdType());
            user.put("idNumber", tuser.getIdNumber());
            user.put("idNumberPhotoUrl", tuser.getIdNumberPhotoUrl());
            user.put("idNumberPhotoBackUrl", tuser.getIdNumberPhotoBackUrl());
            user.put("systemId", tuser.getSystemId());
            user.put("createTime", TimeStampUtil.getTs(tuser.getCreateTime()));
            user.put("updateTime", TimeStampUtil.getTs(tuser.getCreateTime()));
            elec.put("plateNumber", telectrmobile.getPlateNumber());
            elec.put("typeId", telectrmobile.getTypeId());
            elec.put("vendorId", telectrmobile.getVendorId());
            elec.put("purchaseTime", telectrmobile.getPurchaseTime());
            elec.put("colorId", telectrmobile.getColorId());
            elec.put("vin", telectrmobile.getVin());
            elec.put("imei", telectrmobile.getImei());
            elec.put("policyNo", telectrmobile.getPolicyNo());
            elec.put("insuranceTime", telectrmobile.getInsuranceTime());
            elec.put("motorPhotoUrl", telectrmobile.getMotorPhotoUrl());
            elec.put("policyPhotoUrl", telectrmobile.getPolicyPhotoUrl());
            elec.put("promisePhotoUrl", telectrmobile.getPromisePhotoUrl());
            elec.put("createTime", telectrmobile.getCreateTime());
            elec.put("updateTime", telectrmobile.getUpdateTime());
            body.put("registrationId", registration.getRegisterId());
            TinstallSite site = installSiteService.findById(registration.getInstallSiteId());
            if(site != null){
                TaddressRegion region = regionService.findById(site.getRegionId());
                body.put("region", JSONObject.toJSONString(region));
            }
            body.put("user", user);
            body.put("electrombile", elec);
            log.info("lc-register-json:" + body);
            response = restTemplate.postForEntity(lcRegisterUrl, body, JSONObject.class);
        } catch (Exception e) {
            log.error("e", e);
        }
        log.info("register response:" + response);
    }

    /**
     * 通知第三方平台修改备案
     */
    private void broadcastUpdateRegister3TdPlatform(Tregistration registration) {
        ResponseEntity<JSONObject> response = null;
        try {
            JSONObject body = new JSONObject();
            JSONObject user = new JSONObject();
            JSONObject elec = new JSONObject();
            Tuser tuser = registration.getUser();
            Telectrmobile telectrmobile = registration.getElectrombile();
            user.put("name", registration.getUser().getName());
            user.put("contactPhone", tuser.getContactPhone());
            user.put("phone", tuser.getPhone());
            user.put("home", tuser.getHome());
            user.put("idType", tuser.getIdType());
            user.put("idNumber", tuser.getIdNumber());
            user.put("idNumberPhotoUrl", tuser.getIdNumberPhotoUrl());
            user.put("idNumberPhotoBackUrl", tuser.getIdNumberPhotoBackUrl());
            user.put("systemId", tuser.getSystemId());
            user.put("createTime", TimeStampUtil.getTs(tuser.getCreateTime()));
            user.put("updateTime", new Date());
            elec.put("plateNumber", telectrmobile.getPlateNumber());
            elec.put("typeId", telectrmobile.getTypeId());
            elec.put("vendorId", telectrmobile.getVendorId());
            elec.put("purchaseTime", telectrmobile.getPurchaseTime());
            elec.put("colorId", telectrmobile.getColorId());
            elec.put("vin", telectrmobile.getVin());
            elec.put("imei", telectrmobile.getImei());
            elec.put("policyNo", telectrmobile.getPolicyNo());
            elec.put("insuranceTime", telectrmobile.getInsuranceTime());
            elec.put("motorPhotoUrl", telectrmobile.getMotorPhotoUrl());
            elec.put("policyPhotoUrl", telectrmobile.getPolicyPhotoUrl());
            elec.put("promisePhotoUrl", telectrmobile.getPromisePhotoUrl());
            elec.put("createTime", telectrmobile.getCreateTime());
            elec.put("updateTime", telectrmobile.getUpdateTime());
            body.put("registrationId", registration.getRegisterId());
            body.put("user", user);
            body.put("electrombile", elec);
            log.info("lc-update-register-json:" + body);
            response = restTemplate.postForEntity(lcupdateRegisterUrl, body, JSONObject.class);
        } catch (Exception e) {
            log.error("e", e);
        }
        log.info("update register response:" + response);
    }

    /**
     * 通知第三方平台更换设备
     */
    private void broadcastchangeImei3TdPlatform(Long registrationId, String newImei) {
        ResponseEntity<JSONObject> response = null;
        try {
            JSONObject body = new JSONObject();
            body.put("registrationId", registrationId);
            body.put("newImei", newImei);
            log.info("lc-changeimei-json:" + body);
            response = restTemplate.postForEntity(lcChangeImeiUrl, body, JSONObject.class);
        } catch (Exception e) {
            log.error("e", e);
        }
        log.info("changmeimei response:" + response);
    }

    /**
     * 通知第三方平台删除备案
     */
    private void broadCastDeleteRegister3TdPlatform(Long id) {
        ResponseEntity<JSONObject> response = null;
        try {
            JSONObject body = new JSONObject();
            body.put("registrationId", id);
            log.info("lc-delete-register-json:" + body);
            response = restTemplate.postForEntity(lcDeleteRegisterUrl, body, JSONObject.class);
        } catch (RestClientException e) {
            log.error("e", e);
        }
        log.info("delete register response:" + response);
    }

    /**
     * 添加excel标题
     */
    private void addExcelTitle(ExportExcelData data) {
        List<String> titles = new ArrayList<>();
        titles.add("安装点");
        titles.add("imei");
        titles.add("保单号");
        titles.add("车牌号");
        titles.add("车主姓名");
        titles.add("户籍地址");
//        titles.add("联系方式");
        titles.add("身份证号");
        titles.add("注册时间");
        titles.add("登录账号");
        titles.add("车辆种类");
        titles.add("品牌型号");
        titles.add("颜色");
        titles.add("车架号");
        titles.add("购车日期");
        titles.add("安装点负责人");
        titles.add("安装点电话");
        titles.add("派出所");
        titles.add("安装上线");
        titles.add("运营商");
        data.setTitles(titles);
    }

}
