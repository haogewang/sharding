package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.Treservation;
import com.szhq.iemp.reservation.api.service.ReservationService;
import com.szhq.iemp.reservation.api.vo.query.ReservationQuery;
import com.szhq.iemp.reservation.repository.ReservationRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@CacheConfig(cacheNames = "aepreservation")
@Transactional
public class ReservationServiceImpl implements ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);
    @Resource
    private ReservationRepository reservationRepository;

    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;

    @Cacheable
    @Override
    public MyPage<Treservation> findReservationNoCriteria(Integer page, Integer size, String sorts, String orders) {
        Sort sort = SortUtil.sort(sorts, orders, "reservationId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Treservation> pages = reservationRepository.findAll(pageable);
        return new MyPage<Treservation>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Cacheable
    @Override
    public MyPage<Treservation> findReservationCriteria(Integer page, Integer size, String sorts, String orders,
                                                                    ReservationQuery reservationQuery) {
        Sort sort = SortUtil.sort(sorts, orders, "reservationId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Treservation> pages = reservationRepository.findAll(new Specification<Treservation>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Treservation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (reservationQuery != null) {
                    if (reservationQuery.getReservationTime() != null) {
                        Date date = new Date(reservationQuery.getReservationTime());
                        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        long startstamp = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                        LocalDate endDate = localDate.plusDays(1);
                        Date endDates = new Date(java.sql.Date.valueOf(endDate).getTime() - 1L);
                        long endstamp = endDates.getTime();
                        list.add(criteriaBuilder.between(root.get("reservationTime").as(Long.class), startstamp, endstamp));
                    }
                    if (reservationQuery.getStartTime() != null && reservationQuery.getEndTime() != null) {
                        long start = reservationQuery.getStartTime().getTime();
                        long end = reservationQuery.getEndTime().getTime();
                        list.add(criteriaBuilder.between(root.get("reservationTime").as(Long.class), start, end));
                    }
                    if (null != reservationQuery.getOwnerName() && !"".equals(reservationQuery.getOwnerName())) {
                        list.add(criteriaBuilder.equal(root.get("ownerName").as(String.class), reservationQuery.getOwnerName()));
                    }
                    if (!StringUtils.isEmpty(reservationQuery.getPhone())) {
                        list.add(criteriaBuilder.equal(root.get("phone").as(String.class), reservationQuery.getPhone()));
                    }
                    if (!StringUtils.isEmpty(reservationQuery.getIdNumber())) {
                        list.add(criteriaBuilder.equal(root.get("idNumber").as(String.class), reservationQuery.getIdNumber()));
                    }
                    if (!StringUtils.isEmpty(reservationQuery.getReservationNumber())) {
                        list.add(criteriaBuilder.equal(root.get("reservationNumber").as(String.class), reservationQuery.getReservationNumber()));
                    }
                    if (reservationQuery.getOperatorIdList() != null && reservationQuery.isHaveInstallSite() == true) {
                        list.add(root.get("installSite").get("operatorId").as(Integer.class).in(reservationQuery.getOperatorIdList()));
                    }
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return new MyPage<Treservation>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public Integer update(Treservation reservation) {
        Optional<Treservation> treservation = reservationRepository.findById(reservation.getReservationId());
        if (treservation.isPresent()) {
            Treservation model = treservation.get();
            BeanUtils.copyProperties(reservation, model, PropertyUtil.getNullProperties(reservation));
            Treservation entity = reservationRepository.save(model);
            deleteRedisKey();
            if (entity != null) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Integer save(Treservation reservation) {
        Treservation model = reservationRepository.save(reservation);
        deleteRedisKey();
        if (model != null) {
            return 1;
        }
        return 0;
    }

    @CacheEvict
    @Override
    public Treservation delete(Integer id) {
        reservationRepository.deleteById(id);
        deleteRedisKey();
        return null;
    }

    @Override
    public Integer countByExample(Integer registrationSiteId, long start, long end) {
        Integer count = reservationRepository.countByExample(registrationSiteId, start, end);
        return count;
    }

    @Override
    public Treservation getInfo(String number) {
        Treservation treservation = reservationRepository.findByReservationNumberEquals(number);
        return treservation;
    }

    @Override
    public Integer deleteThreeDaysAgoData() {
        long current = System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        long threedayAgo = zero - 24 * 60 * 60 * 1000 * 3;
        return reservationRepository.deleteByReservationTimeLessThan(threedayAgo);
    }

    @Override
    public int deleteByReserNo(String reservationNo) {
        return reservationRepository.deleteByReserNo(reservationNo);
    }

    @Override
    public Treservation findByVin(String vin) {
        return reservationRepository.findByVin(vin);
    }

    @Override
    public List<Treservation> findByMotorNumber(String motorNumber) {
        return reservationRepository.findByMotorNumber(motorNumber);
    }

    @Override
    public Treservation findByPhone(String phone) {
        return reservationRepository.findByPhone(phone);
    }

    @Override
    public void deleteRedisKey() {
        if (redisUtil.keys(CommonConstant.RESERVATION_PATTERN) != null && redisUtil.keys(CommonConstant.RESERVATION_PATTERN).size() > 0) {
            logger.info("keys [" + CommonConstant.RESERVATION_PATTERN + "] exist......");
            Set<String> sets = redisUtil.keys(CommonConstant.RESERVATION_PATTERN);
            for (String key : sets) {
                redisUtil.del(key);
            }
        }
    }

}
