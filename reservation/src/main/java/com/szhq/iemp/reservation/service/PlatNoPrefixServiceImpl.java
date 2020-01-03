package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.reservation.api.model.TplatenoPrefix;
import com.szhq.iemp.reservation.api.service.PlateNoPrefixService;
import com.szhq.iemp.reservation.api.vo.Region;
import com.szhq.iemp.reservation.repository.PlateNoPrefixRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class PlatNoPrefixServiceImpl implements PlateNoPrefixService {

    @Resource
    private PlateNoPrefixRepository plateNoPrefixRepository;

//    @Override
//    public List<ToperatorPlatenoPrefix> getPlateNoPrefixByOperatorId(Integer operatorId) {
//        return operatorPlateNoPrefixService.findPlateNoPrefixByOperatorId(operatorId);
//    }

    @Override
    public List<TplatenoPrefix> findAllPlateNoPrefixByQuery(Region regionQuery) {
        return plateNoPrefixRepository.findAll(new Specification<TplatenoPrefix>() {
            @Override
            public Predicate toPredicate(Root<TplatenoPrefix> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (regionQuery != null) {
                    if (StringUtils.isNotEmpty(regionQuery.getProvinceId())) {
                        list.add(criteriaBuilder.equal(root.get("provinceId").as(String.class), regionQuery.getProvinceId()));
                    }
                    if (StringUtils.isNotEmpty(regionQuery.getCityId())) {
                        list.add(criteriaBuilder.equal(root.get("cityId").as(String.class), regionQuery.getCityId()));
                    }
                    if (StringUtils.isNotEmpty(regionQuery.getRegionId())) {
                        list.add(criteriaBuilder.equal(root.get("regionId").as(String.class), regionQuery.getRegionId()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
    }

    @Override
    public TplatenoPrefix save(TplatenoPrefix entity) {
        String provinceId = entity.getProvinceId();
        String cityId = entity.getCityId();
        String regionId = entity.getRegionId();
        if(StringUtils.isEmpty(provinceId) && StringUtils.isEmpty(cityId) && StringUtils.isEmpty(regionId)){
            log.error("参数错误." + provinceId + "," + cityId + "," + regionId);
            throw new NbiotException(400, "参数错误");
        }
        if(StringUtils.isNotEmpty(provinceId)
                && StringUtils.isNotEmpty(cityId) && StringUtils.isNotEmpty(regionId)){
            if(Objects.equals(provinceId, cityId) || Objects.equals(regionId, cityId) || Objects.equals(provinceId, regionId)){
                if(!"419001".equals(cityId)){
                    log.error("参数错误." + provinceId + "," + cityId + "," + regionId);
                    throw new NbiotException(400, "参数错误");
                }
            }
        }
        return plateNoPrefixRepository.save(entity);
    }

    @Override
    public Integer deleteById(Integer id) {
        return plateNoPrefixRepository.deleteByPrefixId(id);
    }

    @Override
    public TplatenoPrefix findById(Integer id) {
        return plateNoPrefixRepository.findById(id).orElse(null);
    }
}
