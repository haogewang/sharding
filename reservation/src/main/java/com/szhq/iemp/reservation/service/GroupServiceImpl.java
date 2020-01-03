package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.Tgroup;
import com.szhq.iemp.reservation.api.service.GroupService;
import com.szhq.iemp.reservation.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class GroupServiceImpl implements GroupService {

	@Resource
	private GroupRepository groupRepository;

	@Override
	public Tgroup findByIdAndType(Integer id, Integer type) {
		Tgroup group = new Tgroup();
		List<Map<String, Object>> lists = groupRepository.findByGroupIdAndType(id,type);
		if(lists != null && !lists.isEmpty()){
			Map<String, Object> map = lists.get(0);
			Integer parentId = null;
			Integer groupId = Integer.valueOf(String.valueOf(map.get("id")));
			if(StringUtils.isNotEmpty(String.valueOf(map.get("parent_id"))) && !"null".equals(String.valueOf(map.get("parent_id")))){
				parentId = Integer.valueOf(String.valueOf(map.get("parent_id")));
			}
			Integer operatorId = Integer.valueOf(String.valueOf(map.get("operator_id")));
			Integer customType = null;
			if(StringUtils.isNotEmpty(String.valueOf(map.get("custom_type"))) && !"null".equals(String.valueOf(map.get("custom_type")))){
				customType = Integer.valueOf(String.valueOf(map.get("custom_type")));
			}
			String name = String.valueOf(map.get("name"));
			group.setName(name);
			group.setType(type);
			group.setId(groupId);
			group.setOperatorId(operatorId);
			group.setParentId(parentId);
			group.setCustomType(customType);
		}
		return group;
	}
}
