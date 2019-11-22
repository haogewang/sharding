package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.TuserPush;

public interface UserPushService {


    TuserPush add(TuserPush userPush);

    Integer delete(String id);

    /**
     * 根据userId删除
     */
    Integer deleteByUserId(String userId);

}
