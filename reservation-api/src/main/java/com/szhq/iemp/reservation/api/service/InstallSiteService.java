package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TinstallSite;

public interface InstallSiteService {

    /**
     * 根据id查找安装点
     */
    TinstallSite findById(Integer id);

}
