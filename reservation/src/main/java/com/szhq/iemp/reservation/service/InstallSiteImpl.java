package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.TinstallSite;
import com.szhq.iemp.reservation.api.service.InstallSiteService;
import com.szhq.iemp.reservation.repository.InstallSiteRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class InstallSiteImpl implements InstallSiteService {


    @Resource
    private InstallSiteRepository installSiteRepository;

    @Override
    public TinstallSite findById(Integer id) {
        return installSiteRepository.findByInstallSiteId(id);
    }

}
