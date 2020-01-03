package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.ElectrombileExceptionEnum;
import com.szhq.iemp.common.constant.enums.ElectrombileStatusEnum;
import com.szhq.iemp.common.constant.enums.exception.RegisterExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.ListTranscoder;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.service.*;
import com.szhq.iemp.reservation.api.vo.TelectrmobileVo;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import com.szhq.iemp.reservation.repository.ElectrombileRepository;
import com.szhq.iemp.reservation.util.PinyinUtil;
import com.szhq.iemp.reservation.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@CacheConfig(cacheNames = "aepElectrombile")
public class ElectrmobileServiceImpl implements ElectrmobileService {

    private static final Logger logger = LoggerFactory.getLogger(ElectrmobileServiceImpl.class);

    @Resource
    private ElectrombileRepository electrombileRepository;

    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private ElectrmobileColorService electrombileColorService;
    @Autowired
    private ElecmobileVenderService electrombileVendorService;
    @Autowired
    private ElecmobileTypeService elecmobileTypeService;
    @Autowired
    private ElecmobileUserService elecmobileUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    @Qualifier("primaryRedisUtil")
    private RedisUtil redisUtil;


    @Override
    @Cacheable(unless = "#result == null || #result.getTotal() == 0")
    public MyPage<Telectrmobile> findElecByCriteria(Integer page, Integer size, String sorts, String orders, ElecmobileQuery elecQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "electrmobileId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Telectrmobile> pages = electrombileRepository.findAll(new Specification<Telectrmobile>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Telectrmobile> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (elecQuery != null) {
                    //车牌号码
                    if (StringUtils.isNotEmpty(elecQuery.getPlateNo())) {
                        list.add(criteriaBuilder.like(root.get("plateNumber").as(String.class), "%" + elecQuery.getPlateNo() + "%"));
                    }
                    if (StringUtils.isNotEmpty(elecQuery.getPlateNumber())) {
                        list.add(criteriaBuilder.equal(root.get("plateNumber").as(String.class), elecQuery.getPlateNumber()));
                    }
                    //车主姓名
                    if (StringUtils.isNotEmpty(elecQuery.getOwnerName())) {
                        list.add(criteriaBuilder.like(root.get("ownerName").as(String.class), "%" + elecQuery.getOwnerName() + "%"));
                    }
                    if (StringUtils.isNotEmpty(elecQuery.getDevname())) {
                        list.add(criteriaBuilder.like(root.get("devname").as(String.class), "%" + elecQuery.getDevname() + "%"));
                    }
                    if (elecQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(elecQuery.getOperatorIdList()));
                    }
                    if (StringUtils.isNotEmpty(elecQuery.getImei())) {
                        list.add(criteriaBuilder.equal(root.get("imei").as(String.class), elecQuery.getImei()));
                    }
                    if (StringUtils.isNotEmpty(elecQuery.getModelNo())) {
                        list.add(criteriaBuilder.equal(root.get("modelNo").as(String.class), elecQuery.getModelNo()));
                    }
                    if (StringUtils.isNotEmpty(elecQuery.getUserId())) {
                        list.add(criteriaBuilder.equal(root.get("ownerId").as(String.class), elecQuery.getUserId()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<Telectrmobile>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public Telectrmobile createElectrombile(Telectrmobile electrombile) {
        if (StringUtils.isEmpty(electrombile.getPlateNumber())) {
            logger.error("plateNo can not be null." + JSONObject.toJSONString(electrombile));
            //ElectrombileExceptionEnum.E_0004
            throw new NbiotException(3000002, "");
        }
        int count = electrombileRepository.countByPlateNumber(electrombile.getPlateNumber());
        if (count > 0) {
            logger.error("plateNo has exist.plateNo:" + electrombile.getPlateNumber());
            //ElectrombileExceptionEnum.E_0005
            throw new NbiotException(3000003, "");
        }
        Telectrmobile newElectrombile;
        try {
            newElectrombile = save(electrombile);
        } catch (Exception e) {
            logger.error("create elecmobile error.", e);
            //RegisterExceptionEnum.E_0006.getCode()
            throw new NbiotException(10000003, "");
        }
        return newElectrombile;
    }

    @Override
    public Telectrmobile findByImei(String imei) {
//        String elecString = (String) redisUtil.get(CommonConstant.ELEC_IMEI + imei);
//        if (StringUtils.isEmpty(elecString)) {
//            Telectrmobile electrombile = electrombileRepository.findByImei(imei);
//            if (electrombile != null) {
//                redisUtil.set(CommonConstant.ELEC_IMEI + imei, JSONObject.toJSONString(electrombile));
//            }
//            return electrombile;
//        }
//        logger.info("get elec data from redis by imei.imei:" + imei);
//        Telectrmobile electrombile = JSONObject.parseObject(elecString, Telectrmobile.class);
        Telectrmobile electrombile = electrombileRepository.findByImei(imei);
        return electrombile;
    }

    @Override
    public Telectrmobile findByPlateNumber(String plateNumber) {
        String elecString = (String) redisUtil.get(CommonConstant.ELEC_PLATENUMBER + plateNumber);
        if (StringUtils.isEmpty(elecString)) {
            Telectrmobile elec = electrombileRepository.findByPlateNumber(plateNumber);
            if (elec != null) {
                redisUtil.set(CommonConstant.ELEC_PLATENUMBER + plateNumber, JSONObject.toJSONString(elec), 1, TimeUnit.DAYS);
                return elec;
            }
        }
        logger.info("get elec data from redis by plateNo.plateNo:" + plateNumber);
        Telectrmobile elec = JSONObject.parseObject(elecString, Telectrmobile.class);
        return elec;
    }

    @Override
    public List<Telectrmobile> findByVin(String vin) {
        return electrombileRepository.findByVin(vin);
    }

    @Override
    @Cacheable
    public List<TelectrombileVendor> getElecmobileVendors() {
        return electrombileVendorService.findAll();
    }

    @Override
    @Cacheable
    public List<TelectrombileColor> getElecmobileColors() {
        return electrombileColorService.findAll();
    }

    @Override
    @Cacheable
    public List<TelectrombileType> getElecmobileTypes() {
        return elecmobileTypeService.findAll();
    }

    @Override
    public List<Telectrmobile> findByMotorNumber(String motorNumber) {
        return electrombileRepository.findByMotorNumber(motorNumber);
    }

    @Override
    public Map<String, String> getPlateNoByImeis(List<String> imeis) {
        List<Map<String, Object>> lists = electrombileRepository.getPlateNoByImeis(imeis);
        Map<String, String> result = new HashMap<>();
        if (lists != null && !lists.isEmpty()) {
            for (Map<String, Object> map : lists) {
                String imei = (String) map.get("imei");
                String plateNo = (String) map.get("plate_number");
                result.put(imei, plateNo);
            }
        }
        return result;
    }

    @Override
    public List<Telectrmobile> getAllElecmobileByUserId(String userId) {
        List<Telectrmobile> list = new ArrayList<>();
        List<Tregistration> registerList = registrationService.findByUserId(userId);
        Tuser user = userService.findById(userId);
        if(registerList != null && !registerList.isEmpty()){
            List<String> imeis = registerList.stream().map(Tregistration::getImei).filter(x -> x != null).collect(Collectors.toList());
            if(!imeis.isEmpty()){
                List<Telectrmobile> result = findByImeis(imeis);
                if(!result.isEmpty()){
                    setDeviceAndUserInfoToElec(list, user, result);
                }
            }
        }
        return list;
    }

    @Override
    public List<Telectrmobile> getAllElecmobileByUserIdAndType(String userId, String type) {
        List<Telectrmobile> result = new ArrayList<>();
        List<Tregistration> registerList = registrationService.findByUserId(userId);
        if(registerList != null && !registerList.isEmpty()){
            Tuser user = userService.findById(userId);
            List<String> imeis = registerList.stream().map(Tregistration::getImei).filter(x -> x != null).collect(Collectors.toList());
            if(!imeis.isEmpty()){
                List<Telectrmobile> list = findByImeisAndType(imeis, type);
                if(!list.isEmpty()){
                    setDeviceAndUserInfoToElec(result, user, list);
                }
            }
        }
        return result;
    }

    @Override
    public List<Telectrmobile> findAllElecByUserId(String userId, String type, List<Integer> operatorIds, Boolean isApp) {
        List<Telectrmobile> list = new ArrayList<>();
        logger.info("isApp:" + isApp + ".operatorIds:" + JSONObject.toJSONString(operatorIds));
        Tuser user = userService.findById(userId);
        if (isApp) {
            List<Telectrmobile> electrmobiles = findByUserIdAndType(userId, type);
            setDeviceAndUserInfoToElec(list, user, electrmobiles);
        }
        else {
            List<TelectrombileUser> electrombileUsers = elecmobileUserService.findByUserIdAndOperatorIds(userId, operatorIds);
            if(electrombileUsers != null && !electrombileUsers.isEmpty()){
                List<Long> elecIds = electrombileUsers.stream().map(TelectrombileUser::getElectrombileId).collect(Collectors.toList());
                List<Telectrmobile> electrmobiles = electrombileRepository.findAllElecsByElecIdIn(elecIds);
                setDeviceAndUserInfoToElec(list, user, electrmobiles);
            }
        }
        return list;
    }

    private void setDeviceAndUserInfoToElec(List<Telectrmobile> list, Tuser user, List<Telectrmobile> electrmobiles) {
        if (electrmobiles != null && !electrmobiles.isEmpty()) {
            for (Telectrmobile elec : electrmobiles) {
                TdeviceInventory device = deviceInventoryService.findByImei(elec.getImei());
                elec.setDeviceInventory(device);
                elec.setUser(user);
                if(elec.getOperatorId() != null){
                    Toperator toperator = operatorService.findById(elec.getOperatorId());
                    if (toperator != null) {
                        elec.setAdressRegion(toperator.getRegion());
                    }
                }
                list.add(elec);
            }
        }
    }

    private List<Telectrmobile> findByImeis(List<String> imeis) {
        return electrombileRepository.findByImeiIn(imeis);
    }

    private List<Telectrmobile> findByImeisAndType(List<String> imeis, String type) {
        return electrombileRepository.findAllElectrombilesByImeisInAndType(imeis, type);
    }

    private List<Telectrmobile> findByUserIdAndType(String userId, String type) {
        List<TelectrombileUser> electrombileUsers = elecmobileUserService.findByUserId(userId);
        if(electrombileUsers != null && !electrombileUsers.isEmpty()){
            List<Long> elecIds = electrombileUsers.stream().map(TelectrombileUser::getElectrombileId).collect(Collectors.toList());
            return electrombileRepository.findAllElectrombilesByElecIdInAndType(elecIds, type);
        }
        return null;
    }

    @Override
    public Telectrmobile getElecAndUserInfoByPlateNo(ElecmobileQuery elecQuery) {
        String plateNo = elecQuery.getPlateNumber();
        if(StringUtils.isEmpty(plateNo)){
            throw new NbiotException(3000002, "车牌号不能为空");
        }
        Telectrmobile electrombile = null;
        logger.info("elecQuery plateNo:" + JSONObject.toJSONString(elecQuery));
        if (elecQuery.getOperatorIdList() != null) {
            electrombile = electrombileRepository.findByPlateNumberAndOperatorIdsIn(plateNo, elecQuery.getOperatorIdList());
            if(electrombile != null){
                electrombile.setUser(userService.findById(electrombile.getOwnerId()));
                electrombile.setDeviceInventory(deviceInventoryService.findByImei(electrombile.getImei()));
            }
        }
        else {
            electrombile = findByPlateNumber(plateNo);
            if(electrombile != null){
                electrombile.setUser(userService.findById(electrombile.getOwnerId()));
                electrombile.setDeviceInventory(deviceInventoryService.findByImei(electrombile.getImei()));
            }
        }
        return electrombile;
    }

    @Override
    public String getBfStatus(String imei) {
        Telectrmobile electrombile = findByImei(imei);
        if (electrombile != null) {
            return electrombile.getEmbileBfState();
        }
        return null;
    }

    @Override
    public Telectrmobile findByElecId(Long id) {
        String elecString = (String) redisUtil.get(CommonConstant.ELEC_ID + id);
        if (StringUtils.isEmpty(elecString)) {
            Telectrmobile elec = electrombileRepository.findByElecId(id);
            if (elec != null) {
                redisUtil.set(CommonConstant.ELEC_ID + id, JSON.toJSONString(elec));
            }
            return elec;
        }
        logger.info("get elec data from redis.id:" + id);
        Telectrmobile elec = JSONObject.parseObject(elecString, Telectrmobile.class);
        return elec;
    }

    @Override
    public Integer updatePolicyNoByImei(String policyNo, String imei) {
        Integer i = electrombileRepository.updatePolicyNoByImei(policyNo, imei);
        return i;
    }

    @Override
    public Integer batchImportExcelUpdatePolicyNo(MultipartFile file) {
        Integer count = 0;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            Workbook wb;
            if (fileName.endsWith("xlsx")) {
                wb = new XSSFWorkbook(inputStream);
            } else {
                wb = new HSSFWorkbook(inputStream);
            }
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                throw new NbiotException(500, "Excel数据为空");
            }
            //列数
            int column = sheet.getRow(0).getPhysicalNumberOfCells();
            logger.info("Excel列数:" + column);
            //行数
            int rows = sheet.getLastRowNum();
            logger.info("Excel行数:" + rows);
            List<Telectrmobile> list = new ArrayList<>();
            //循环Excel
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Telectrmobile electrombile = new Telectrmobile();
                //第i行第一列
                if (row.getCell(0) != null) {
                    String imei = row.getCell(0).getStringCellValue();
                    if (imei == null || imei.isEmpty()) {
                        throw new NbiotException(500, "Excel中imei不能为空，请填写后再进行上传");
                    }
                    electrombile.setImei(imei);
                }
                //第i行第二列
                if (row.getCell(1) != null) {
                    String policyNo = row.getCell(1).getStringCellValue();
                    if (policyNo == null || policyNo.isEmpty()) {
                        throw new NbiotException(500, "Excel中保单号不能为空，请填写后再进行上传");
                    }
                    electrombile.setPolicyNo(policyNo);
                }
                list.add(electrombile);
            }
            for (Telectrmobile elec : list) {
                Integer i = updatePolicyNoByImei(elec.getPolicyNo(), elec.getImei());
                count += i;
            }
            deleteElecRedisData();
        } catch (Exception e) {
            logger.error("e", e);
            throw new NbiotException(500, "导入失败");
        }
        return count;
    }

    @Override
    public Integer updateBfStatus(String imei, boolean mode) {
        /*
         *  勿删此代码
         * Map<String,Object> map = new HashMap<String,Object>(); map.put("1", imei);
         * map.put("2", mode); ResponseEntity<Result> response =
         * this.restTemplate.postForEntity( domain +
         * "/iemp-nbiot-web/device/tracker-mode?imei={1}&mode={2}", HttpMethod.POST,
         * Result.class, map); logger.info("status:" +response.getStatusCodeValue() +
         * ",body :" + response.getBody());
         *
         * if(response.getBody().getCode() == 0) { logger.error("POST " + domain +
         * "/iemp-nbiot-web/device/tracker-mode method failed, body is:" +
         * response.getBody()); throw new NbiotException(500,
         * ElectrombileExceptionEnum.E_0003.getMessage()); }
         */
        Telectrmobile electrombile = findByImei(imei);
        if (electrombile != null) {
            if (ElectrombileStatusEnum.NORMAL.getCode() == mode) {
                electrombile.setEmbileBfState(ElectrombileStatusEnum.NORMAL.getMessage());
                logger.info("update bfstatus normal,imei is:" + imei);
            } else {
                electrombile.setEmbileBfState(ElectrombileStatusEnum.UNNORMAL.getMessage());
                logger.info("update bfstatus unNormal,imei is:" + imei);
            }
            save(electrombile);
            redisUtil.del(CommonConstant.ELEC_IMEI + imei);
            return 1;
        }
        return 0;
    }

    @Override
    public Integer updateBkStatus(String imei, boolean mode) {
        Telectrmobile electrombile = findByImei(imei);
        if (electrombile != null) {
            if (ElectrombileStatusEnum.NORMAL.getCode() == mode) {
                electrombile.setEmbileBkState(ElectrombileStatusEnum.NORMAL.getMessage());
                logger.info("update bkstatus normal,imei is:" + imei);
            } else {
                electrombile.setEmbileBkState(ElectrombileStatusEnum.UNNORMAL.getMessage());
                logger.info("update bkstatus unNormal,imei is:" + imei);
            }
            save(electrombile);
            return 1;
        }
        return 0;
    }

    @Override
    public Telectrmobile save(Telectrmobile electrombile) {
        Telectrmobile elec = electrombileRepository.save(electrombile);
        return elec;
    }

    @Override
    public Integer deleteByElecId(Long id) {
        logger.info("delete elecmobile id is:" + id);
        int i = electrombileRepository.deleteByElecId(id);
        return i;
    }

    @Override
    public void deleteByImei(String imei) {
        electrombileRepository.deleteByImei(imei);
        logger.info("delete electrombile success,imei is:" + imei);
    }

    @Override
    public Telectrmobile findByIotDeviceId(String deviceId) {
        TdeviceInventory deviceInventory = deviceInventoryService.findByIotDeviceId(deviceId);
        if (deviceInventory != null) {
            Telectrmobile elec = findByImei(deviceInventory.getImei());
            return elec;
        }
        return null;
    }

    @Override
    public TelectrombileColor findByColorId(Integer id) {
        return electrombileColorService.findById(id);
    }

    @Override
    public TelectrombileVendor findByVendorId(Integer id) {
        return electrombileVendorService.findById(id);
    }

    @Override
    public TelectrombileType findByTypeId(Integer id) {
        return elecmobileTypeService.findById(id);
    }

    @Override
    public Long countTodayInstalledEquip(Integer installSiteId) {
        return null;
    }

    @Override
    public List<TelectrombileVendor> getTypeByIndex(String index) {
        ElecmobileQuery query = new ElecmobileQuery();
        query.setIndex(index);
        return electrombileVendorService.findByCretia(query);
    }

    @Override
    public TelectrombileColor addColors(TelectrombileColor entity) {
        if (entity == null) {
            return null;
        }
        delRedisKey(CommonConstant.ELEC_COLORS_PATTERN);
        return electrombileColorService.addColors(entity);
    }

    @Override
    public TelectrombileVendor addVendors(TelectrombileVendor entity) {
        if (entity == null) {
            return null;
        }
        delRedisKey(CommonConstant.ELEC_VENDORS_PATTERN);
        String index = PinyinUtil.toFirstChar(PinyinUtil.toPinyin(entity.getName()));
        if (index != null) {
            entity.setSearchIndex(index.toUpperCase());
        }
        return electrombileVendorService.addVendors(entity);
    }

    @Override
    public TelectrombileType addTypes(TelectrombileType entity) {
        if (entity == null) {
            return null;
        }
        delRedisKey(CommonConstant.ELEC_TYPES_PATTERN);
        return elecmobileTypeService.addTypes(entity);
    }

    @Override
    public Integer deleteElecColorById(Integer id) {
        delRedisKey(CommonConstant.ELEC_COLORS_PATTERN);
        return electrombileColorService.deleteElecColorById(id);
    }

    @Override
    public Integer deleteElecVendorById(Integer id) {
        delRedisKey(CommonConstant.ELEC_VENDORS_PATTERN);
        return electrombileVendorService.deleteElecVendorById(id);
    }

    @Override
    public Integer deleteElecTypeById(Integer id) {
        delRedisKey(CommonConstant.ELEC_TYPES_PATTERN);
        return elecmobileTypeService.deleteElecTypeById(id);
    }

    @Override
    public Integer deleteElecRedisData() {
        int count = 0;
        if (redisUtil.keys(CommonConstant.ELEC_PATTERN) != null && redisUtil.keys(CommonConstant.ELEC_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.ELEC_PATTERN);
            for (String key : sets) {
                logger.info("del redis key [" + key + "]");
                redisUtil.del(key);
                count ++;
            }
        }
        return count;
    }

    @Override
    public void setViewDateByImei(String imei, Date date) {
        Telectrmobile elecmobile = findByImei(imei);
        if (elecmobile != null) {
            electrombileRepository.updateViewDate(date, elecmobile.getElectrmobileId());
            delRedisKeyByElec(elecmobile);
        }else {
            throw new NbiotException(400002, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public void setViewDateByImeis(List<String> imeis, Date date) {
        if(imeis != null && !imeis.isEmpty()){
//            for(String imei : imeis){
//                setViewDateByImei(imei, date);
//            }
           Integer count =  electrombileRepository.updateViewDateByImeis(imeis, date);
           logger.info("update view date count:" + count);
        }
    }

    @Override
    public void setNameByImei(String imei, String name) {
        Telectrmobile electrombile = findByImei(imei);
        if (electrombile != null) {
            electrombile.setName(name);
            save(electrombile);
            delRedisKeyByElec(electrombile);
        } else {
            throw new NbiotException(404, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public void setFrequencyByImei(String imei, Integer frequency) {
        Telectrmobile electrmobile = findByImei(imei);
        if (electrmobile != null) {
            electrmobile.setFrequency(frequency);
            save(electrmobile);
            delRedisKeyByElec(electrmobile);
        } else {
            throw new NbiotException(404, DeviceExceptionEnum.E_0000.getMessage());
        }
    }

    @Override
    public Integer deleteRedisColorTypeVendor() {
        Set<String> colorSets = redisUtil.keys(CommonConstant.ELEC_COLORS_PATTERN);
        deleteRedisSet(colorSets);
        Set<String> typeSets = redisUtil.keys(CommonConstant.ELEC_TYPES_PATTERN);
        deleteRedisSet(typeSets);
        Set<String> vendorSets = redisUtil.keys(CommonConstant.ELEC_VENDORS_PATTERN);
        deleteRedisSet(vendorSets);
        return null;
    }

    private void delRedisKeyByElec(Telectrmobile electrombile) {
        String key1 = CommonConstant.ELECALL_ID_KEY + "*" + electrombile.getElectrmobileId() + "*";
        String key2 = CommonConstant.ELEC_IMEI + electrombile.getImei();
        Set<String> sets1 = redisUtil.keys(key1);
        Set<String> sets2 = redisUtil.keys(key2);
        if (sets1 != null && sets1.size() > 0) {
            deleteRedisSet(sets1);
        }
        if (sets2 != null && sets2.size() > 0) {
            deleteRedisSet(sets2);
        }
    }

    /**
     * 删除redis的key值
     */
    private void delRedisKey(String key) {
        if (redisUtil.hasKey(key)) {
            redisUtil.del(key);
            logger.info("redis delete key [" + key + "] success");
        }
    }

    private void deleteRedisSet(Set<String> sets1) {
        for (String s : sets1) {
            logger.info("del redis key [" + s + "]");
            redisUtil.del(s);
        }
    }

}
