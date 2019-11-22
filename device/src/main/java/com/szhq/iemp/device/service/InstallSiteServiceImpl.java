package com.szhq.iemp.device.service;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.SiteExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.ExportExcelUtils;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.common.vo.ExportExcelData;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TaddressRegion;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.model.TinstallSite;
import com.szhq.iemp.device.api.model.TpolicePrecinct;
import com.szhq.iemp.device.api.service.*;
import com.szhq.iemp.device.api.vo.InstallSiteAndWorker;
import com.szhq.iemp.device.api.vo.InstallSiteDeviceCount;
import com.szhq.iemp.device.api.vo.RegisterVo;
import com.szhq.iemp.device.api.vo.query.InstallSiteQuery;
import com.szhq.iemp.device.repository.InstallSiteRepository;
import com.szhq.iemp.device.util.RedisUtil;
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
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanghao
 * @date 2019/10/18
 */
@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "aepInstallSite")
public class InstallSiteServiceImpl implements InstallSiteService {

    @Resource
    private InstallSiteRepository installSiteRepository;

    @Autowired
    private PolicePrecinctService policeService;
    @Autowired
    private DeviceInventoryService deviceInventoryService;
    @Autowired
    private InstallSiteScoreService installSiteScoreService;
    @Autowired
    private AddressRegionService regionService;
    @Autowired
    private ElectrmobileService electrmobileService;

    @Autowired
    private RedisUtil redisUtil;

    @Cacheable(unless = "#result == null|| #result.getTotal() == 0")
    @Override
    public MyPage<TinstallSite> findAllByCriteria(Integer page, Integer size, String sorts, String orders, InstallSiteQuery siteQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "installSiteId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TinstallSite> pages = installSiteRepository.findAll(new Specification<TinstallSite>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TinstallSite> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (siteQuery != null) {
                    if (siteQuery.getRegionId() != null) {
                        list.add(criteriaBuilder.equal(root.get("regionId").as(Integer.class), siteQuery.getRegionId()));
                    }
                    if (siteQuery.getPoliceId() != null) {
                        list.add(criteriaBuilder.equal(root.get("policeId").as(String.class), siteQuery.getPoliceId()));
                    }
                    if (siteQuery.getStatus() != null) {
                        list.add(criteriaBuilder.equal(root.get("status").as(Boolean.class), siteQuery.getStatus()));
                    }
                    if (siteQuery.getInstallSiteId() != null) {
                        list.add(criteriaBuilder.equal(root.get("installSiteId").as(Integer.class), siteQuery.getInstallSiteId()));
                    }
                    if (siteQuery.getInstallSiteName() != null) {
                        list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + siteQuery.getInstallSiteName() + "%"));
                    }
                    if (siteQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(siteQuery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TinstallSite>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public TinstallSite findByName(String name) {
        return installSiteRepository.findByName(name);
    }

    @Override
    public TinstallSite add(TinstallSite entity) {
        TinstallSite installSite = findByName(entity.getName());
        if (installSite != null && Objects.equals(installSite.getOperatorId(), entity.getOperatorId())) {
            log.error("site name exist.operatorId:" + entity.getOperatorId());
            throw new NbiotException(SiteExceptionEnum.E_0004.getCode(), SiteExceptionEnum.E_0004.getMessage());
        }
        if (entity.getPoliceId() != null) {
            TpolicePrecinct policePrecinct = policeService.findById(entity.getPoliceId());
            if (policePrecinct != null) {
                entity.setPoliceName(policePrecinct.getPoliceStation());
            }
        }
        if(entity.getRegionId() != null){
            TaddressRegion region = regionService.findById(entity.getRegionId());
            if (region != null) {
                entity.setRegionName(region.getAreaName());
            }
        }
        deleteInstallSiteRedisData();
        return installSiteRepository.save(entity);
    }

    @Override
    public Integer update(TinstallSite entity) {
        if (entity.getInstallSiteId() == null) {
            log.error("site id can not be null.");
            throw new NbiotException(400, "");
        }
        TinstallSite installSite = findById(entity.getInstallSiteId());
        if(installSite == null){
            log.error("site is not exist.siteId:" + entity.getInstallSiteId());
            throw new NbiotException(SiteExceptionEnum.E_0005.getCode(), SiteExceptionEnum.E_0005.getMessage());
        }
        if (entity.getPoliceId() != null && !Objects.equals(entity.getPoliceId(), installSite.getPoliceId())) {
            TpolicePrecinct policePrecinct = policeService.findById(entity.getPoliceId());
            if (policePrecinct != null) {
                entity.setPoliceName(policePrecinct.getPoliceStation());
            }else{
                log.error("police is not found.siteName:" + installSite.getName());
            }
        }
        if (entity.getRegionId() != null && !Objects.equals(entity.getRegionId(), installSite.getRegionId())) {
            TaddressRegion region = regionService.findById(entity.getRegionId());
            if (region != null) {
                entity.setRegionName(region.getAreaName());
            }
        }
        if(StringUtils.isNotEmpty(entity.getName()) && !Objects.equals(entity.getName(), installSite.getName())){
            Integer i = deviceInventoryService.updateSiteNameBySiteId(entity.getName(), entity.getInstallSiteId());
            Integer j = electrmobileService.updateSiteNameBySiteId(entity.getName(), entity.getInstallSiteId());
            deviceInventoryService.deleteDeviceRedisData();
            electrmobileService.deleteElecRedisData();
            log.info("update site name success. device:{}, elec:{}", i, j);
        }
        BeanUtils.copyProperties(entity, installSite, PropertyUtil.getNullProperties(entity));
        entity = installSiteRepository.save(installSite);
        log.info("update installSite success, id is:" + entity.getInstallSiteId());
        deleteInstallSiteRedisData();
        return entity.getInstallSiteId();
    }

    @Override
    public Integer deleteById(Integer id) {
        validIsExistDeviceUnderInstallSite(id);
        Integer count = installSiteScoreService.deleteBySiteId(id);
        log.info("delete site score count:{},siteId:{}",count, id);
        return installSiteRepository.deleteByInstallSiteId(id);
    }

    @Override
    public TinstallSite findById(Integer installSiteId) {
        return installSiteRepository.findById(installSiteId).orElse(null);
    }

    @Override
    public void setMaxReservationNumBySiteId(Integer siteId, Integer value) {
        TinstallSite tinstallSite = findById(siteId);
        if (tinstallSite != null) {
            tinstallSite.setMaxReservationCount(value);
            installSiteRepository.save(tinstallSite);
            deleteInstallSiteRedisData();
        } else {
            throw new NbiotException(404, SiteExceptionEnum.E_0005.getMessage());
        }
    }

    @Override
    public void setMaxReservationDaysBySiteId(Integer siteId, Integer value) {
        TinstallSite tinstallSite = findById(siteId);
        if (tinstallSite != null) {
            tinstallSite.setMaxReservationDay(value);
            installSiteRepository.save(tinstallSite);
            deleteInstallSiteRedisData();
        } else {
            throw new NbiotException(404, SiteExceptionEnum.E_0005.getMessage());
        }
    }

    @Override
    public Integer getTotalDeviceBySiteId(Integer id) {
        Integer count = installSiteRepository.getTotalDeviceBySiteId(id);
        return count;
    }

    @Override
    public Integer getEquipDeviceBySiteId(Integer id) {
        Integer count = installSiteRepository.getEquipDeviceBySiteId(id);
        return count;
    }

    @Override
    public Integer getOnlineEquipDeviceBySiteId(Integer id) {
        Integer count = installSiteRepository.getOnlineEquipDeviceBySiteId(id);
        return count;
    }

    @Override
    public Integer getTodayInstalledCountBySiteId(Integer id) {
        Integer count = installSiteRepository.getTodayInstalledCountBySiteId(id);
        return count;
    }

    @Override
    public Integer getTodayOnlineCountBySiteId(Integer id) {
        Integer count = installSiteRepository.getTodayOnlineCountBySiteId(id);
        return count;
    }

    @Override
    public Double getScoreBySiteId(Integer id) {
        Double count = installSiteRepository.getScoreBySiteId(id);
        return count;
    }

    @Override
    public List<TinstallSite> getSitesByRegionIdLike(String id) {
        List<Map<String, Object>> lists = installSiteRepository.findByAdressRegionIdLike(id);
        List<TinstallSite> result = new ArrayList<>();
        if (lists != null && !lists.isEmpty()) {
            for (Map<String, Object> map : lists) {
                TinstallSite site = new TinstallSite();
                Integer siteId = (Integer) map.get("install_site_id");
                String name = (String) map.get("name");
                Integer operatorId = (Integer) map.get("operator_id");
                Boolean status = (Boolean) map.get("status");
                Integer regionId = (Integer) map.get("region_id");
                String regionName = (String) map.get("regionName");
                String lat = (String) map.get("lat");
                String lon = (String) map.get("lon");
                String address = (String) map.get("address");
                site.setStatus(status);
                site.setAddress(address);
                site.setInstallSiteId(siteId);
                site.setLat(lat);
                site.setLon(lon);
                site.setName(name);
                site.setOperatorId(operatorId);
                site.setRegionId(regionId);
                site.setRegionName(regionName);
                result.add(site);
            }
        }
        return result;
    }

    @Override
    public List<TinstallSite> getSitesByCityId(Integer id, Boolean status) {
        return installSiteRepository.getSitesByCityId(id, status);
    }

    @Override
    public List<TinstallSite> getSitesByRegionId(Integer id, Boolean status) {
        return installSiteRepository.getSitesByRegionId(id, status);
    }

    @Override
    public List<InstallSiteDeviceCount> countByOffsetAndInstallSiteId(Integer installSiteId, Integer offset) {
        List<InstallSiteDeviceCount> list = new ArrayList<InstallSiteDeviceCount>();
        List<Map<String, Object>> lists = installSiteRepository.countByOffsetAndInstallSiteId(offset, installSiteId);
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                InstallSiteDeviceCount deviceCount = new InstallSiteDeviceCount();
                Date date = (Date) map.get("create_time");
                Long totalInstalled = Long.valueOf(map.get("total_installed").toString());
                Long totalOnline = Long.valueOf(map.get("onlineCount").toString());
                deviceCount.setDate(date);
                deviceCount.setTotalInstalledCount(totalInstalled);
                deviceCount.setTotalOnlineCount(totalOnline);
                if (totalInstalled != 0) {
                    double c = new BigDecimal((float) totalOnline / totalInstalled).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    log.debug("c:" + c);
                    deviceCount.setTotalOnRate(c);
                } else {
                    deviceCount.setTotalOnRate(0d);
                }
                list.add(deviceCount);
            }
        }
        return list;
    }

    @Override
    public List<InstallSiteDeviceCount> countByCondation(InstallSiteQuery query) {
        List<InstallSiteDeviceCount> counts = new ArrayList<InstallSiteDeviceCount>();
        List<Map<String, Object>> lists = new ArrayList<>();
        if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() == null) {
//			log.info("startTime:{},endTime:{}",query.getStartTime(), query.getEndTime());
            lists = installSiteRepository.countByDate(query.getStartTime(), query.getEndTime());
        } else if (query != null && query.getStartTime() != null && query.getEndTime() != null && query.getOperatorIdList() != null) {
            lists = installSiteRepository.countByDate(query.getStartTime(), query.getEndTime(), query.getOperatorIdList());
        } else if (query != null && query.getStartTime() != null && query.getEndTime() != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() == null) {
            lists = installSiteRepository.countByCondition(query.getStartTime(), query.getEndTime(), query.getInstallSiteName());
        } else if (query != null && query.getStartTime() != null && query.getEndTime() != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() != null) {
            lists = installSiteRepository.countByCondition(query.getStartTime(), query.getEndTime(), query.getInstallSiteName(), query.getOperatorIdList());
        } else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() == null) {
            lists = installSiteRepository.countByInstallSiteName(query.getInstallSiteName());
        } else if (query != null && StringUtils.isNotEmpty(query.getInstallSiteName()) && query.getOperatorIdList() != null) {
            lists = installSiteRepository.countByInstallSiteName(query.getInstallSiteName(), query.getOperatorIdList());
        } else if (query != null && query.getOperatorIdList() != null) {
            lists = installSiteRepository.countAll(query.getOperatorIdList());
        } else {
            lists = installSiteRepository.countAll();
        }
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                InstallSiteDeviceCount installSiteDeviceCount = new InstallSiteDeviceCount();
                Integer installSiteId = (Integer) map.get("install_site_id");
                String installSiteName = (String) map.get("site_name");
                Long totalInstalled = Long.valueOf(map.get("total_installed").toString());
                Long totalOnline = Long.valueOf(map.get("total_online").toString());
                Long ctInstalled = Long.valueOf(map.get("ct_installed").toString());
                Long ctOnline = Long.valueOf(map.get("ct_online").toString());
                Long cmccInstalled = Long.valueOf(map.get("cmcc_installed").toString());
                Long cmccOnline = Long.valueOf(map.get("cmcc_online").toString());
                Long cuccInstalled = Long.valueOf(map.get("cucc_installed").toString());
                Long cuccOnline = Long.valueOf(map.get("cucc_online").toString());
                installSiteDeviceCount.setTotalInstalledCount(Long.valueOf(totalInstalled));
                installSiteDeviceCount.setTotalOnlineCount(totalOnline);
                installSiteDeviceCount.setCmccInstalledCount(cmccInstalled);
                installSiteDeviceCount.setCmccOnlineCount(cmccOnline);
                installSiteDeviceCount.setCtInstalledCount(ctInstalled);
                installSiteDeviceCount.setCtOnlineCount(ctOnline);
                installSiteDeviceCount.setCuInstalledCount(cuccInstalled);
                installSiteDeviceCount.setCuOnlineCount(cuccOnline);
                installSiteDeviceCount.setInstallSiteId(installSiteId);
                installSiteDeviceCount.setInstallSiteName(installSiteName);
                if (totalInstalled != 0) {
                    double c = new BigDecimal((float) totalOnline / totalInstalled).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    installSiteDeviceCount.setTotalOnRate(c);
                } else {
                    installSiteDeviceCount.setTotalOnRate(0d);
                }
                counts.add(installSiteDeviceCount);
            }
        }
        log.debug("counts:" + counts.size());
        return counts;
    }

    @Override
    public List<InstallSiteDeviceCount> installSiteOrder(Integer offset, InstallSiteQuery query) {
        List<Map<String, Object>> lists = null;
        if (query != null && query.getOperatorIdList() != null) {
            lists = installSiteRepository.installSiteOrder(offset, query.getOperatorIdList());
        } else {
            lists = installSiteRepository.installSiteOrder(offset);
        }
        List<InstallSiteDeviceCount> counts = new ArrayList<>();
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                InstallSiteDeviceCount installSiteDeviceCount = new InstallSiteDeviceCount();
                Date createTime = (Date) map.get("createTime");
                Long totalInstalled = Long.valueOf(map.get("totalInstalled").toString());
                String installSiteName = (String) map.get("installSiteName");
                Integer installSiteId = (Integer) map.get("installSiteId");
                installSiteDeviceCount.setTotalInstalledCount(totalInstalled);
                installSiteDeviceCount.setDate(createTime);
                installSiteDeviceCount.setInstallSiteId(installSiteId);
                installSiteDeviceCount.setInstallSiteName(installSiteName);
                counts.add(installSiteDeviceCount);
            }
        }
        return counts;
    }

    @Override
    public List<InstallSiteAndWorker> getSiteInstalledCount(List<Integer> operatorIds, Integer offset) {
        List<InstallSiteAndWorker> result = new ArrayList<>();
        List<Map<String, Object>> lists = null;
        if(operatorIds != null && operatorIds.get(0) != 0){
            lists = installSiteRepository.getSiteInstalledCount(offset);
        }
        else if(operatorIds != null){
            lists = installSiteRepository.getSiteInstalledCount(operatorIds, offset);
        }
        if(lists != null && !lists.isEmpty()){
            for(Map<String, Object> map : lists){
                InstallSiteAndWorker installSiteAndWorker = new InstallSiteAndWorker();
                Integer siteId = Integer.valueOf(String.valueOf(map.get("install_site_id")));
                String siteName = String.valueOf(map.get("name"));
                String userId = String.valueOf(map.get("id"));
                String userName = String.valueOf(map.get("username"));
                Integer count = Integer.valueOf(String.valueOf(map.get("installedCount")));
                installSiteAndWorker.setWorkerName(userName);
                installSiteAndWorker.setInstallSiteName(siteName);
                installSiteAndWorker.setInstallSiteId(siteId);
                installSiteAndWorker.setTotalCount(count);
                installSiteAndWorker.setWorkerId(userId);
                result.add(installSiteAndWorker);
            }
        }
        return result;
    }

    @Override
    public void export(Integer siteId, HttpServletResponse response) {
        List<TdeviceInventory> result = new ArrayList<>();
        List<TdeviceInventory> devices = deviceInventoryService.findAllByInstallSiteId(siteId);
        if(devices != null && !devices.isEmpty()){
            List<String> imeis = devices.stream().map(TdeviceInventory::getImei).collect(Collectors.toList());
            Map<String, RegisterVo> map = deviceInventoryService.getInstalledWorkerByImeis(imeis);
            for(TdeviceInventory device : devices){
                if(map != null){
                    device.setRegisterVo(map.get(device.getImei()));
                }
                result.add(device);
            }
            ExportExcelData data = addExcelData(result);
            try {
                String excelName = "安装点设备列表.xls";
                ExportExcelUtils.exportExcel(response, excelName, data);
            } catch (Exception e) {
                log.error("e", e);
                throw new NbiotException(500, "");
            }
        }
    }

    @Override
    public Long countByQuery(InstallSiteQuery siteQuery) {
        Long count = installSiteRepository.count(new Specification<TinstallSite>() {
            private static final long serialVersionUID = 1294079993545745792L;
            @Override
            public Predicate toPredicate(Root<TinstallSite> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (siteQuery != null) {
                    if (siteQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(siteQuery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return count;
    }

    @Override
    public Integer deleteInstallSiteRedisData() {
        int count = 0;
        if (redisUtil.keys(CommonConstant.SITE_PATTERN) != null && redisUtil.keys(CommonConstant.SITE_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.SITE_PATTERN);
            for (String key : sets) {
                log.info("del redis key [" + key + "]");
                redisUtil.del(key);
                count ++;
            }
        }
        return count;
    }

    /**
     * 验证安装点下是否有设备
     */
    private void validIsExistDeviceUnderInstallSite(Integer id) {
        int count = deviceInventoryService.getDeviceNumByInstallSiteId(id);
        if (count > 0) {
            throw new NbiotException(SiteExceptionEnum.E_0003.getCode(), SiteExceptionEnum.E_0003.getMessage());
        }
    }

    private ExportExcelData addExcelData(List<TdeviceInventory> result) {
        ExportExcelData data = new ExportExcelData();
        data.setName("安装点设备列表");
        addExcelTitle(data);
        List<List<String>> rows = new LinkedList<>();
        if (result != null && result.size() > 0) {
            addExcelCellData(result, rows);
            log.info("export devices size is:" + result.size());
            data.setRows(rows);
        }
        return data;
    }

    private void addExcelTitle(ExportExcelData data) {
        List<String> titles = new ArrayList<>();
        titles.add("安装点");
        titles.add("imei");
        titles.add("SN");
        titles.add("ICCID");
        titles.add("所属运营商");
        titles.add("当前归属地");
        titles.add("设备安装状态");
        titles.add("安装人员");
        titles.add("安装时间");
        data.setTitles(titles);
    }

    private void addExcelCellData(List<TdeviceInventory> result, List<List<String>> rows) {
        for (TdeviceInventory view : result) {
            List<String> row = new LinkedList<>();
            row.add(view.getInstallSiteName());
            row.add(view.getImei());
            row.add(view.getSnNo());
            row.add(view.getIccid());
            row.add(view.getIsp());
            row.add(view.getRegionName());
            switch (view.getDevstate()){
                case 0:
                    row.add("未安装");
                case 1:
                    row.add("已安装");
                case 2:
                    row.add("更换");
            }
            if(view.getRegisterVo() != null){
                row.add(view.getRegisterVo().getInstallWorkerName());
                if(view.getRegisterVo().getDate() != null){
                    row.add(view.getRegisterVo().getDate().toString());
                }
            }
            rows.add(row);
        }
    }
}
