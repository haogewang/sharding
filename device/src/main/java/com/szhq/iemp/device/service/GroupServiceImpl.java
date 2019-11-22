package com.szhq.iemp.device.service;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.DeviceExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.GroupExceptionEnum;
import com.szhq.iemp.common.constant.enums.exception.OperatorExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.SortUtil;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.model.Telectrmobile;
import com.szhq.iemp.device.api.model.Tgroup;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.service.DeviceInventoryService;
import com.szhq.iemp.device.api.service.ElectrmobileService;
import com.szhq.iemp.device.api.service.GroupService;
import com.szhq.iemp.device.api.service.OperatorService;
import com.szhq.iemp.device.api.vo.UserAndElecInfo;
import com.szhq.iemp.device.api.vo.query.GroupQuery;
import com.szhq.iemp.device.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GroupServiceImpl implements GroupService {
	
	@Resource
	private GroupRepository groupRepository;

	@Autowired
	private OperatorService operatorService;
	@Autowired
	private DeviceInventoryService deviceInventoryService;
	@Autowired
	private ElectrmobileService electrmobileService;

	@Override
	public MyPage<Tgroup> findGroupByCriteria(Integer page, Integer size, String sorts, String orders, GroupQuery myQuery) {
		Sort sort = SortUtil.sort(sorts, orders, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Tgroup> pages = groupRepository.findAll(new Specification<Tgroup>(){
			private static final long serialVersionUID = 1L;
			@Override
			public Predicate toPredicate(Root<Tgroup> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> list = new ArrayList<Predicate>();
				if(myQuery != null) {
					if(myQuery.getGroupName() != null){
						list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + myQuery.getGroupName() + "%"));
					}
					if (myQuery.getOperatorIdList() != null) {
						list.add(root.get("operatorId").as(Integer.class).in(myQuery.getOperatorIdList()));
					}
					//1:设备分组 2:车辆分组
					if(myQuery.getType() != null){
						list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), myQuery.getType()));
					}
					//组属性（如：大屏显示）
					if(myQuery.getCustomType() != null){
						list.add(criteriaBuilder.equal(root.get("customType").as(Integer.class), myQuery.getCustomType()));
					}
				}
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		}, pageable);
		return new MyPage<Tgroup>(pages.getContent(),pages.getTotalElements(),pages.getNumber(),pages.getSize());
	}

	@Override
	public List<Tgroup> getAllDeviceGroupChildrenById(Integer parentId) {
		if(Objects.equals(0, parentId)){
			return findAll(1);
		}
		return findAllChildren(parentId);
	}

	@Override
	public List<Tgroup> getAllElecGroupChildrenById(Integer parentId) {
		if(Objects.equals(0, parentId)){
			return findAll(2);
		}
		return findAllChildren(parentId);
	}

	@Override
	public List<UserAndElecInfo> getUserAndElecInfoByImeis(List<String> imeis) {
		List<UserAndElecInfo> result = new ArrayList<>();
		List<Map<String, Object>> lists = groupRepository.getUserAndElecInfoByImeis(imeis);
		if(lists != null && !lists.isEmpty()){
			for (Map<String, Object> map : lists) {
				UserAndElecInfo userAndElecInfo = new UserAndElecInfo();
				String username = (String) map.get("username");
				String plateNumber = (String) map.get("plate_number");
				String imei = (String) map.get("imei");
				userAndElecInfo.setUsername(username);
				userAndElecInfo.setPlateNumber(plateNumber);
				userAndElecInfo.setImei(imei);
				result.add(userAndElecInfo);
			}
		}
		return result;
	}

	@Override
	public List<Tgroup> getNextElecGroupById(Integer id) {
		List<Tgroup> result = new ArrayList<>();
		List<Map<String, Object>>  lists = groupRepository.findNextGroups(id, 2);
		getNextGroup(result, lists);
		return result;
	}

	@Override
	public List<Tgroup> getNextDeviceGroupById(Integer id) {
		List<Tgroup> result = new ArrayList<>();
		List<Map<String, Object>>  lists = groupRepository.findNextGroups(id, 1);
		getNextGroup(result, lists);
		return result;
	}

	private void getNextGroup(List<Tgroup> result, List<Map<String, Object>> lists) {
		if (lists != null && !lists.isEmpty()) {
			for (Map<String, Object> map : lists) {
				Tgroup group = new Tgroup();
				Integer groupId = Integer.valueOf(String.valueOf(map.get("id")));
				Integer parentId = Integer.valueOf(String.valueOf(map.get("parent_id")));
				Integer operatorId = Integer.valueOf(String.valueOf(map.get("operator_id")));
				String name = String.valueOf(map.get("name"));
				group.setId(groupId);
				group.setName(name);
				group.setOperatorId(operatorId);
				group.setParentId(parentId);
				result.add(group);
			}
		}
	}

	@Override
	public Tgroup save(Tgroup entity) {
		if(entity.getOperatorId() == null){
			log.error("group id can not be null.group name:" + entity.getName());
			throw new NbiotException(400, "");
		}
		Toperator operator = operatorService.findById(entity.getOperatorId());
		if(operator == null){
			log.error("group is not exist.group name:" + entity.getName());
			throw new NbiotException(600001, OperatorExceptionEnum.E_0002.getMessage());
		}
		if (findByName(entity.getName()) != null && entity.getId() == null) {
			log.error("group name already exist!groupName:" + entity.getName());
			throw new NbiotException(500009, GroupExceptionEnum.E_00013.getMessage());
		}
		if(entity.getParentId() != null){
			Tgroup parent = findById(entity.getParentId());
			if(parent == null){
				log.error("parent group is not exist.group name:" + entity.getName());
				throw new NbiotException(500010, GroupExceptionEnum.E_00011.getMessage());
			}
			if(Objects.equals(parent.getId(), entity.getId())) {
				throw new NbiotException(500011, GroupExceptionEnum.E_00012.getMessage());
			}
		}
		entity.setOperatorName(operator.getName());
		return groupRepository.save(entity);
	}

	@Override
	public Integer deleteDeviceGroupById(Integer id) {
		validGroup(id, 1);
		Integer count = deviceInventoryService.countByGroupId(id);
		if(count > 0){
			log.error("group has dispatch devices.can not delete it.id:" + id);
			throw new NbiotException(500001, GroupExceptionEnum.E_0003.getMessage());
		}
		Integer i = groupRepository.deleteByGroupId(id);
		return i;
	}

	@Override
	public Integer deleteElecGroupById(Integer id) {
		validGroup(id, 2);
		Integer count = electrmobileService.countByGroupId(id);
		if(count > 0){
			log.error("group has dispatch elecs.can not delete it.id:" + id);
			throw new NbiotException(500001, GroupExceptionEnum.E_0003.getMessage());
		}
		Integer i = groupRepository.deleteByGroupId(id);
		return i;
	}

	@Override
	public Tgroup findByName(String name) {
		return groupRepository.findByName(name);
	}

	@Override
	public Tgroup findById(Integer id) {
		return groupRepository.findById(id).orElse(null);
	}

	@Override
	public Integer dispatchToDeviceGroup(List<String> imeis, Integer groupId) {
		Tgroup group = validGroup(groupId, 1);
		List<TdeviceInventory> devices = deviceInventoryService.findByImeiIn(imeis);
		if(devices != null && !devices.isEmpty()){
			List<Integer> groupIds = devices.stream().map(TdeviceInventory::getGroupId).filter(x -> x !=null).collect(Collectors.toList());
			if(!groupIds.isEmpty()){
				log.error("some devices has dispatch group.groupId:" + JSONObject.toJSONString(groupIds));
				throw new NbiotException(500007, GroupExceptionEnum.E_0009.getMessage());
			}
			devices.forEach(p -> {
				p.setGroupId(groupId);
			});
			deviceInventoryService.saveAll(devices);
		}
		return devices.size();
	}

	@Override
	public Integer dispatchToElecGroup(List<String> imeis, Integer groupId) {
		Tgroup group = validGroup(groupId, 2);
		List<Telectrmobile> electrmobiles = electrmobileService.findByImeis(imeis);
		if(electrmobiles != null && !electrmobiles.isEmpty()){
			List<Integer> groupIds = electrmobiles.stream().map(Telectrmobile::getGroupId).filter(x -> x !=null).collect(Collectors.toList());
			if(!groupIds.isEmpty()){
				log.error("some elecs has dispatch group.groupId:" + JSONObject.toJSONString(groupIds));
				throw new NbiotException(500007, GroupExceptionEnum.E_0009.getMessage());
			}
			electrmobiles.forEach(p -> {
				p.setGroupId(groupId);
			});
			electrmobileService.saveAll(electrmobiles);
		}
		return electrmobiles.size();
	}

	@Override
	public Integer removeDeviceGroup(List<String> imeis) {
		Integer count = deviceInventoryService.removeGroupByImeis(imeis);
		deviceInventoryService.deleteDeviceRedisData();
		return count;
	}

	@Override
	public Integer removeElecGroup(List<String> imeis) {
		Integer count = electrmobileService.removeGroupByImeis(imeis);
		electrmobileService.deleteElecRedisData();
		return count;
	}

	private List<Tgroup> findAllChildren(Integer parentId) {
		List<Tgroup> groups = new ArrayList<>();
		findGroupsById(parentId, groups);
		groups.add(findById(parentId));
		return groups;
	}

	private void findGroupsById(Integer parentId, List<Tgroup> groups) {
//		List<Tgroup>  lists = groupRepository.findNextGroups(parentId);
		List<Map<String, Object>> lists = groupRepository.findNextGroups(parentId);
		List<Tgroup>  result = new ArrayList<>();
		getNextGroup(result, lists);
		if(result != null && result.size() > 0) {
			for(Tgroup group : result) {
				groups.add(group);
				findGroupsById(group.getId(), groups);
			}
		}
	}

	private Tgroup validGroup(Integer groupId, Integer type) {
		Tgroup group = findById(groupId);
		if (group == null) {
			log.error("group is not exist.id:" + groupId);
			throw new NbiotException(500002, GroupExceptionEnum.E_0004.getMessage());
		}
		if(!Objects.equals(type, group.getType())){
			log.error("group type is not right.id:" + groupId);
			throw new NbiotException(500008, GroupExceptionEnum.E_00010.getMessage());
		}
		return group;
	}

	private List<Tgroup> findAll(Integer type) {
		return groupRepository.findAll(new Specification<Tgroup>(){
			@Override
			public Predicate toPredicate(Root<Tgroup> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> list = new ArrayList<Predicate>();
				//1:设备分组 2:车辆分组
				list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), type));
				Predicate[] p = new Predicate[list.size()];
				return criteriaBuilder.and(list.toArray(p));
			}
		});
	}
}
