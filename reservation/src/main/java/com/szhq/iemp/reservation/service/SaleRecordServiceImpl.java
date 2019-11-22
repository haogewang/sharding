package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TsaleRecord;
import com.szhq.iemp.reservation.api.service.SaleRecordService;
import com.szhq.iemp.reservation.repository.SaleRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
@Transactional
@Slf4j
public class SaleRecordServiceImpl implements SaleRecordService {

	@Resource
	private SaleRecordRepository saleRecordRepository;


	@Override
	public TsaleRecord add(TsaleRecord entity) {
		return saleRecordRepository.save(entity);
	}

	@Override
	public TsaleRecord findByImeiAndMode(String imei, Integer mode) {
		return saleRecordRepository.findByImeiAndMode(imei, mode);
	}
}
