package com.szhq.iemp.device.service;

import com.szhq.iemp.common.util.TimeStampUtil;
import com.szhq.iemp.device.api.model.TsaleRecord;
import com.szhq.iemp.device.api.service.OperatorService;
import com.szhq.iemp.device.api.service.SaleRecordService;
import com.szhq.iemp.device.api.vo.ActiveDeviceCount;
import com.szhq.iemp.device.api.vo.query.SaleRecordQuery;
import com.szhq.iemp.device.repository.SaleRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class SaleRecordServiceImpl implements SaleRecordService {

	@Resource
	private SaleRecordRepository saleRecordRepository;
	@Autowired
	private OperatorService operatorService;

//	@Override
//	public TsaleRecord add(TsaleRecord entity) {
//		return saleRecordRepository.save(entity);
//	}
//
//	@Override
//	public TsaleRecord findByImeiAndMode(String imei, Integer mode) {
//		return saleRecordRepository.findByImeiAndMode(imei, mode);
//	}
//
//	@Override
//	public List<ActiveDeviceCount> saleStatisticByOperatorId(SaleRecordQuery query) {
//		if(query.getOperatorId() == null){
//			return null;
//		}
//		List<ActiveDeviceCount> result = new ArrayList<>();
//		List<Integer> ids =  operatorService.findAllChildIds(query.getOperatorId());
//		List<Map<String, Object>> saleStatistics = saleRecordRepository.saleStatisticByOperatorId(ids, query.getOffset());
//		getData(result, saleStatistics);
//		return result;
//	}
//
//	@Override
//	public List<ActiveDeviceCount> saleStatisticByGroupId(SaleRecordQuery query) {
//		if(query.getGroupId() == null){
//			return null;
//		}
//		List<ActiveDeviceCount> result = new ArrayList<>();
//		List<Map<String, Object>> saleStatistics = saleRecordRepository.saleStatisticByGroupId(query.getGroupId(), query.getOffset());
//		getData(result, saleStatistics);
//		return result;
//	}
//
//	@Override
//	public Integer countSaleByOperatorIds(List<Integer> operatorIds, Integer type, SaleRecordQuery query) {
//		Integer count = 0;
//		if(query.getStartTime() == null && query.getEndTime() ==null){
//			count = saleRecordRepository.countSaleByOperatorIds(operatorIds, type);
//		}else{
//			count = saleRecordRepository.countSaleByOperatorIds(operatorIds, type, query.getStartTime(), query.getEndTime());
//		}
//		return count;
//	}
//
//	@Override
//	public Integer countSaleByGroupIds(List<String> groupIds, int mode, Date startTime, Date endTime) {
//		Integer count = 0;
//		if(startTime == null && endTime ==null){
//			count = saleRecordRepository.countSaleByGroupIds(groupIds, mode);
//		}else{
//			count = saleRecordRepository.countSaleByGroupIds(groupIds, mode, startTime, endTime);
//		}
//		return count;
//	}
//
//	private void getData(List<ActiveDeviceCount> result, List<Map<String, Object>> saleStatistics) {
//		if (saleStatistics != null && !saleStatistics.isEmpty()) {
//			for (Map<String, Object> map : saleStatistics) {
//				ActiveDeviceCount activeCount = new ActiveDeviceCount();
//				String days = (String) map.get("days");
//				Long activecount = 0L;
//				Long unactivecount = 0L;
//				if (map.get("active_count") != null) {
//					activecount = Long.valueOf(map.get("active_count").toString());
//				}
//				if (map.get("unactive_count") != null) {
//					unactivecount = Long.valueOf((String) map.get("unactive_count").toString());
//				}
//				Date date = TimeStampUtil.parseDate(days, "yyyy-MM-dd");
//				activeCount.setActiveCount(activecount);
//				activeCount.setNoActiveCount(unactivecount);
//				activeCount.setDate(date);
//				result.add(activeCount);
//			}
//		}
//	}
}
