package com.szhq.iemp.device.service;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TaddressRegion;
import com.szhq.iemp.device.api.service.AddressRegionService;
import com.szhq.iemp.device.api.vo.AdressRegionVo;
import com.szhq.iemp.device.api.vo.query.RegionQuery;
import com.szhq.iemp.device.repository.AddressRegionRepository;
import com.szhq.iemp.device.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "aepAddressRegion")
public class AddressRegionServiceImpl implements AddressRegionService {

    @Resource
    private AddressRegionRepository regionRepository;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    @Cacheable(unless="#result == null|| #result.getTotal() == 0")
    public MyPage<TaddressRegion> findByCretira(Integer offset, Integer limit, String sorts, String orders, RegionQuery regionQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(offset, limit, sort);
        Page<TaddressRegion> pages = regionRepository.findAll(new Specification<TaddressRegion>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TaddressRegion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (regionQuery != null) {
                    if (regionQuery.getRegionId()!= null) {
                        list.add(criteriaBuilder.equal(root.get("id").as(Integer.class), regionQuery.getRegionId()));
                    }
                    if (regionQuery.getAddressRegionId()!= null) {
                        list.add(criteriaBuilder.equal(root.get("id").as(Integer.class), regionQuery.getAddressRegionId()));
                    }
                    if (regionQuery.getRegionIds() != null) {
                        list.add(root.get("id").as(Integer.class).in(regionQuery.getRegionIds()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TaddressRegion>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public List<TaddressRegion> findByNameLike(String name) {
        return regionRepository.findByNameLike(name);
    }

    @Override
    public TaddressRegion findById(Integer id) {
        TaddressRegion region = (TaddressRegion)redisUtil.get(CommonConstant.REGION_ID + id);
        if(region == null){
            region = regionRepository.findByRegionId(id);
            redisUtil.set(CommonConstant.REGION_ID + id, region);
            return region;
        }
        log.info("get region data from redis.regionId:" + id);
        return region;
    }

    @Override
    @Cacheable(unless="#result == null|| #result.size() == 0")
    public List<TaddressRegion> findByIds(List<Integer> ids) {
        List<TaddressRegion> regions = regionRepository.findByRegionIds(ids);
        return regions;
    }

    @Override
    @Cacheable(unless="#result == null|| #result.size() == 0")
    public Map<Integer, List<AdressRegionVo>> getNexLevelAllChildrenByIds(List<Integer> ids) {
        Map<Integer, List<AdressRegionVo>> result = new HashMap<Integer, List<AdressRegionVo>>();
        List<TaddressRegion> list = regionRepository.findByRegionIds(ids);
        if (list != null && list.size() > 0) {
            for (TaddressRegion addressRegion : list) {
                List<AdressRegionVo> children = findNexLevelAllChildrenById(addressRegion.getId());
                if (children != null && children.size() > 0) {
                    result.put(addressRegion.getId(), children);
                } else {
                    result.put(addressRegion.getId(), findByRegionId(addressRegion.getId()));
                }
            }
        }
        return result;
    }

    @Override
    @Cacheable(unless="#result == null|| #result.size() == 0")
    public List<AdressRegionVo> findNexLevelAllChildrenById(Integer id) {
        List<AdressRegionVo> addressRegionVoList = new ArrayList<>();
        List<Map<String, Object>> lists = regionRepository.findNexLevelAllChildrenById(id);
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                AdressRegionVo adressRegionVo = new AdressRegionVo();
                Long areaId = Long.valueOf(map.get("id").toString());
                String areaName = (String) map.get("area_name");
                String areaCode = (String) map.get("area_code");
                Long parentId = Long.valueOf(map.get("parent_id").toString());
                String parentAreaName = (String) map.get("parent_area_name");
                String parentAreaCode = (String) map.get("parent_area_code");
                adressRegionVo.setId(areaId);
                adressRegionVo.setAreaName(areaName);
                adressRegionVo.setAreaCode(areaCode);
                adressRegionVo.setParentId(parentId);
                adressRegionVo.setParentAreaCode(parentAreaCode);
                adressRegionVo.setParentAreaName(parentAreaName);
                addressRegionVoList.add(adressRegionVo);
            }
        }
        return addressRegionVoList;
    }

    private List<AdressRegionVo> findByRegionId(Integer id) {
        List<AdressRegionVo> adressRegionVoList = new ArrayList<>();
        TaddressRegion adressRegion = findById(id);
        if (adressRegion != null) {
            AdressRegionVo adressRegionVo = new AdressRegionVo();
            adressRegionVo.setId(Long.valueOf(adressRegion.getId()));
            adressRegionVo.setAreaName(adressRegion.getAreaName());
            adressRegionVo.setAreaCode(adressRegion.getAreaCode());
            if (adressRegion.getRegion() != null) {
                adressRegionVo.setParentId(Long.valueOf(adressRegion.getRegion().getId()));
                adressRegionVo.setParentAreaCode(adressRegion.getRegion().getAreaCode());
                adressRegionVo.setParentAreaName(adressRegion.getRegion().getAreaName());
            }
            adressRegionVoList.add(adressRegionVo);
        }
        return adressRegionVoList;
    }

    @Override
    public List<TaddressRegion> getAllSiteCities() {
        List<Integer> ids = regionRepository.findAllSiteRegionIds();
        List<TaddressRegion> result = getAddressRegions(ids);
        return result;
    }

    @Override
    public List<TaddressRegion> getAllRegionsByCityId(Integer id) {
        List<Integer> ids = regionRepository.getAllRegionsByCityId(id);
        List<TaddressRegion> result = getAddressRegions(ids);
        return result;
    }

    @Override
    public Integer deleteRegionRedisData() {
        if (redisUtil.keys(CommonConstant.REGION_PATTERN) != null && redisUtil.keys(CommonConstant.REGION_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.REGION_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
            }
        }
        return 1;
    }

    private List<TaddressRegion> getAddressRegions(List<Integer> ids) {
        List<TaddressRegion> result = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            for (Integer id : ids) {
                TaddressRegion region = findById(id);
                if (region != null) {
                    region.setRegion(region.getRegion());
                    result.add(region);
                }
            }
        }
        return result;
    }

}
