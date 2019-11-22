package com.szhq.iemp.device.service;

import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.BaseQuery;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TiotType;
import com.szhq.iemp.device.api.service.IotTypeService;
import com.szhq.iemp.device.repository.IotTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@Transactional
public class IotTypeServiceImpl implements IotTypeService {
	
	private static final Logger logger = LoggerFactory.getLogger(IotTypeServiceImpl.class);
	@Resource
	private IotTypeRepository iotTypeRepository;

	@Override
	public TiotType save(TiotType entity) {
		logger.debug("entity:" + entity);
		return iotTypeRepository.save(entity);
	}

	@Override
	public Integer deleteById(Integer id) {
		return iotTypeRepository.deleteByIotTypeId(id);
	}

	@Override
	public TiotType findByName(String name) {
		return iotTypeRepository.findByName(name);
	}
	
	@Override
	public TiotType findById(Integer id) {
		return iotTypeRepository.findById(id).orElse(null);
	}

	@Override
	public MyPage<TiotType> findAllByCriteria(Integer page, Integer size, String sorts, String orders, BaseQuery myQuery) {
		Sort sort = SortUtil.sort(sorts, orders, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<TiotType> pages = iotTypeRepository.findAll(new Specification<TiotType>(){
			private static final long serialVersionUID = 1L;
			@Override
			public Predicate toPredicate(Root<TiotType> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> list = new ArrayList<Predicate>();
				if(myQuery != null) {
					if(myQuery.getIotTypeId() != null){
						list.add(criteriaBuilder.equal(root.get("id").as(Integer.class), myQuery.getIotTypeId()));
					}
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		return new MyPage<TiotType>(pages.getContent(),pages.getTotalElements(),pages.getNumber(),pages.getSize());
	}


}
