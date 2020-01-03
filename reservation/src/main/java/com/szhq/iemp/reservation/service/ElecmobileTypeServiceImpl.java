package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TelectrombileType;
import com.szhq.iemp.reservation.api.model.TelectrombileVendor;
import com.szhq.iemp.reservation.api.service.ElecmobileTypeService;
import com.szhq.iemp.reservation.repository.ElectrombileTypeRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ElecmobileTypeServiceImpl implements ElecmobileTypeService {

    @Resource
    private ElectrombileTypeRepository electrombileTypeRepository;

    @Override
    public List<TelectrombileType> findAll() {
        List<TelectrombileType> result = new ArrayList<>();
        List<TelectrombileType> list = electrombileTypeRepository.findAll();
        if(!list.isEmpty()){
            for(TelectrombileType type : list){
                type.setElectrombileTypeId(type.getTypeId());
                result.add(type);
            }
        }
        return result;
    }

    @Override
    public TelectrombileType findById(Integer id) {
        return electrombileTypeRepository.findById(id).orElse(null);
    }

    @Override
    public TelectrombileType addTypes(TelectrombileType entity) {
        return electrombileTypeRepository.save(entity);
    }

    @Override
    public Integer deleteElecTypeById(Integer id) {
        return electrombileTypeRepository.deleteByTypeId(id);
    }

}
