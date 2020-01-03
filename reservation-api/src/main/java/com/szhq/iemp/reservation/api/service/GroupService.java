package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.Tgroup;

public interface GroupService {

    /**
     * 通过id/type查找
     */
    Tgroup findByIdAndType(Integer id, Integer type);

}
