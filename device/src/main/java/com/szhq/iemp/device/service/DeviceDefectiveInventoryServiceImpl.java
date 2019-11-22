package com.szhq.iemp.device.service;

import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceDefectiveInventory;
import com.szhq.iemp.device.api.service.DeviceDefectiveInventoryService;
import com.szhq.iemp.device.api.vo.query.DeviceQuery;
import com.szhq.iemp.device.repository.DeviceDefectiveInventoryRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DeviceDefectiveInventoryServiceImpl implements DeviceDefectiveInventoryService {

    @Resource
    private DeviceDefectiveInventoryRepository defectiveRepository;

    @Override
    public TdeviceDefectiveInventory save(TdeviceDefectiveInventory entity) {
        return defectiveRepository.save(entity);
    }

    @Override
    public List<TdeviceDefectiveInventory> saveAll(List<TdeviceDefectiveInventory> list) {
        return defectiveRepository.saveAll(list);
    }

    @Override
    public Integer deleteByImei(String imei) {
        return defectiveRepository.deleteByImei(imei);
    }

    @Override
    public TdeviceDefectiveInventory findByImei(String imei) {
        return defectiveRepository.findByImei(imei);
    }

    @Override
    public MyPage<TdeviceDefectiveInventory> findByCretira(Integer offset, Integer limit, String sorts, String orders, DeviceQuery deviceQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "id");
        Pageable pageable = PageRequest.of(offset, limit, sort);
        Page<TdeviceDefectiveInventory> pages = defectiveRepository.findAll(new Specification<TdeviceDefectiveInventory>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TdeviceDefectiveInventory> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (deviceQuery != null) {
                    log.debug("go");
                    if (deviceQuery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(deviceQuery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<TdeviceDefectiveInventory>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

}
