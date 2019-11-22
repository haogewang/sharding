package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.reservation.api.model.TregistrationLog;
import com.szhq.iemp.reservation.api.service.RegistrationLogService;
import com.szhq.iemp.reservation.api.vo.query.RegisterQuery;
import com.szhq.iemp.reservation.repository.RegistrationLogRepository;
import org.apache.commons.lang3.StringUtils;
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
public class RegistrationLogServiceImpl implements RegistrationLogService {

    @Resource
    private RegistrationLogRepository registrationLogRepository;

    @Override
    public Page<TregistrationLog> findRegistrationCriteria(Integer page, Integer size, String sorts, String orders, RegisterQuery rquery) {
        Sort sort = SortUtil.sort(sorts, orders, "registerLogId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TregistrationLog> registrationReservationPage = registrationLogRepository.findAll(new Specification<TregistrationLog>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<TregistrationLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (rquery != null) {
                    if (rquery.getRegisterId() != null) {
                        list.add(criteriaBuilder.equal(root.get("registerId").as(Integer.class), rquery.getRegisterId()));
                    }
                    if (rquery.getType() != null) {
                        list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), rquery.getType()));
                    }
                    if (StringUtils.isNotEmpty(rquery.getImei())) {
                        list.add(criteriaBuilder.equal(root.get("oldImei").as(String.class), rquery.getImei()));
                    }
                    if (StringUtils.isNotEmpty(rquery.getPlateNumber())) {
                        list.add(criteriaBuilder.equal(root.get("oldPlateNo").as(String.class), rquery.getPlateNumber()));
                    }
                    if (rquery.getOperatorIdList() != null) {
                        list.add(root.get("operatorId").as(Integer.class).in(rquery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return registrationReservationPage;
    }

    @Override
    public Integer updateByRegisterId(Long registerId) {
        List<TregistrationLog> result = new ArrayList<>();
        List<TregistrationLog> registerLogs = registrationLogRepository.findByRegisterId(registerId);
        if(registerLogs != null && !registerLogs.isEmpty()){
            for(TregistrationLog registerLog : registerLogs){
                registerLog.setRegisterId(null);
                result.add(registerLog);
            }
            saveAll(result);
        }
        return null;
    }


    @Override
    public TregistrationLog save(TregistrationLog entity) {
        return registrationLogRepository.save(entity);
    }

    public Integer saveAll(List<TregistrationLog> entitys) {
       List<TregistrationLog> list = registrationLogRepository.saveAll(entitys);
        return list.size();
    }

}
