package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TelectrombileVendor;
import com.szhq.iemp.reservation.api.service.ElecmobileVenderService;
import com.szhq.iemp.reservation.api.vo.query.ElecmobileQuery;
import com.szhq.iemp.reservation.repository.ElectrombileVendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

@Slf4j
@Service
@Transactional
public class ElecmobileVenderServiceImpl implements ElecmobileVenderService {

    @Resource
    private ElectrombileVendorRepository electrombileVendorRepository;

    @Override
    public List<TelectrombileVendor> findAll() {
        List<TelectrombileVendor> result = new ArrayList<>();
        List<TelectrombileVendor> list = electrombileVendorRepository.findAll();
        if(list != null && !list.isEmpty()){
            for(TelectrombileVendor vendor : list){
                vendor.setElectrombileVendorId(vendor.getVendorId());
                result.add(vendor);
            }
        }
        return result;
    }

    @Override
    public TelectrombileVendor findById(Integer id) {
        return electrombileVendorRepository.findById(id).orElse(null);
    }

    @Override
    public List<TelectrombileVendor> findByCretia(ElecmobileQuery equery) {
        List<TelectrombileVendor> lists = electrombileVendorRepository.findAll(new Specification<TelectrombileVendor>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TelectrombileVendor> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (equery != null) {
                    //索引
                    if (StringUtils.isNotEmpty(equery.getIndex())) {
                        list.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("searchIndex").as(String.class)), "%" + equery.getIndex().toLowerCase() + "%"));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        });
        return lists;
    }

    @Override
    public TelectrombileVendor addVendors(TelectrombileVendor entity) {
        return electrombileVendorRepository.save(entity);
    }

    @Override
    public Integer deleteElecVendorById(Integer id) {
        return electrombileVendorRepository.deleteElecVendorById(id);
    }


}
