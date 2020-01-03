package com.szhq.iemp.device.service;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.Telectrmobile;
import com.szhq.iemp.device.api.service.ElectrmobileService;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.device.repository.ElectrmobileRepository;
import com.szhq.iemp.device.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wanghao
 * @date 2019/10/18
 */
@Slf4j
@Service
@Transactional
public class ElectrmobileServiceImpl implements ElectrmobileService {

    @Resource
    private ElectrmobileRepository electrmobileRepository;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public MyPage<Telectrmobile> findElecByCriteria(Integer offset, Integer limit, String sorts, String orders, ElecmobileQuery elecQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "electrmobileId");
        Pageable pageable = PageRequest.of(offset, limit, sort);
        Page<Telectrmobile> pages = electrmobileRepository.findAll(new Specification<Telectrmobile>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Telectrmobile> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (elecQuery != null) {
                    //车牌号码
                    if (StringUtils.isNotEmpty(elecQuery.getPlateNo())) {
                        list.add(criteriaBuilder.like(root.get("plateNumber").as(String.class), "%" + elecQuery.getPlateNo() + "%"));
                    }
                    if (elecQuery.getGroupIdList() != null) {
                        list.add(root.get("groupId").as(Integer.class).in(elecQuery.getGroupIdList()));
                    }
                    if (elecQuery.getGroupId() != null) {
                        list.add(criteriaBuilder.equal(root.get("groupId").as(Integer.class), elecQuery.getGroupId()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<Telectrmobile>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public Integer getDeviceNumberByOperatorId(Integer operatorId) {
        return electrmobileRepository.getDeviceNumberByOperatorId(operatorId);
    }

    @Override
    public Long countByCriteria(DeviceQuery deviceQuery) {
        Long count = electrmobileRepository.count(new Specification<Telectrmobile>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Telectrmobile> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (deviceQuery != null) {
                    if (deviceQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(deviceQuery.getOperatorIdList()));
                    }
                    if (deviceQuery.getInstallSiteId() != null) {
                        list.add(criteriaBuilder.equal(root.get("installSiteId").as(Integer.class), deviceQuery.getInstallSiteId()));
                    }
                    if (deviceQuery.getGroupIdList() != null) {
                        list.add(root.get("groupId").as(Integer.class).in(deviceQuery.getGroupIdList()));
                    }
                }
                list.add(criteriaBuilder.isNotNull(root.get("imei").as(String.class)));
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return count;
    }

    @Override
    public Integer countByImei(String imei) {
        return electrmobileRepository.countByImei(imei);
    }

    @Override
    public String getTypeById(Integer typeId) {
        return electrmobileRepository.getTypeById(typeId);
    }

    @Override
    public String getVendorById(Integer vendorId) {
        return electrmobileRepository.getVendorById(vendorId);
    }

    @Override
    public List<String> findImeisByElecIds(List<Long> elecIds) {
        return electrmobileRepository.findImeisByElecIds(elecIds);
    }

    @Override
    public Integer updateStoreNameByStorehouseId(String name, Integer id) {
        return electrmobileRepository.updateStoreNameByStorehouseId(name, id);
    }


    @Override
    public Integer updateSiteNameBySiteId(String name, Integer installSiteId) {
        return electrmobileRepository.updateSiteNameBySiteId(name, installSiteId);
    }

    @Override
    public List<Telectrmobile> findByImeis(List<String> imeis) {
        return electrmobileRepository.findByImeis(imeis);
    }

    @Override
    public Integer saveAll(List<Telectrmobile> electrmobiles) {
        List<Telectrmobile> electrmobileList = electrmobileRepository.saveAll(electrmobiles);
        return electrmobileList.size();
    }

    @Override
    public Integer countByGroupId(Integer id) {
        return electrmobileRepository.countByGroupId(id);
    }

    @Override
    public Integer removeGroupByImeis(List<String> imeis) {
        return electrmobileRepository.removeGroupByImeis(imeis);
    }

    @Override
    public Integer deleteElecRedisData() {
        int count = 0;
        if (redisUtil.keys(CommonConstant.ELEC_PATTERN) != null && redisUtil.keys(CommonConstant.ELEC_PATTERN).size() > 0) {
            Set<String> sets = redisUtil.keys(CommonConstant.ELEC_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
                count ++;
            }
        }
        return count;
    }



}
