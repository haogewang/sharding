package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TelectrombileColor;
import com.szhq.iemp.reservation.api.model.TelectrombileType;
import com.szhq.iemp.reservation.api.service.ElectrmobileColorService;
import com.szhq.iemp.reservation.repository.ElectrombileColorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ElectrombileColorServiceImpl implements ElectrmobileColorService {

    @Resource
    private ElectrombileColorRepository electrombileColorRepository;

    @Override
    public List<TelectrombileColor> findAll() {
        List<TelectrombileColor> result = new ArrayList<>();
        List<TelectrombileColor> list = electrombileColorRepository.findAll();
        if(!list.isEmpty()){
            for(TelectrombileColor color : list){
                color.setElectrombileColorId(color.getColorId());
                result.add(color);
            }
        }
        return result;
    }

    @Override
    public TelectrombileColor findById(Integer id) {
        return electrombileColorRepository.findById(id).orElse(null);
    }

    @Override
    public TelectrombileColor addColors(TelectrombileColor entity) {
        return electrombileColorRepository.save(entity);
    }

    @Override
    public Integer deleteElecColorById(Integer id) {
        return electrombileColorRepository.deleteElecColorById(id);
    }

}
