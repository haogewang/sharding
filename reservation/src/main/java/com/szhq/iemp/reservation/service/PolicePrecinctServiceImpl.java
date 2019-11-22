package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TpolicePrecinct;
import com.szhq.iemp.reservation.api.service.PolicePrecinctService;
import com.szhq.iemp.reservation.repository.PoliceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Slf4j
@Transactional
public class PolicePrecinctServiceImpl implements PolicePrecinctService {

    @Resource
    private PoliceRepository policeRepository;

    @Override
    public List<TpolicePrecinct> findAll() {
        return policeRepository.findAll();
    }

    @Override
    public TpolicePrecinct add(TpolicePrecinct entity) {
        return policeRepository.save(entity);
    }

    @Override
    public Integer deleteById(String id) {
        log.info("delete police station. id:" + id);
        policeRepository.deleteById(id);
        return 1;
    }

    @Override
    public TpolicePrecinct findById(String id) {
        return policeRepository.findByIdString(id);
    }


}
