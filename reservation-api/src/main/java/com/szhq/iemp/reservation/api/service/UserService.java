package com.szhq.iemp.reservation.api.service;

import com.szhq.iemp.reservation.api.model.Tuser;

import java.util.List;

public interface UserService {

    Tuser findByIdNumber(String idNumber);

    Tuser findByPhone(String phone);

    List<Tuser> findByOwnerName(String ownerName);

    Tuser add(Tuser user);

    Tuser updateUser(Tuser user);

    Integer delete(String id);

    Tuser findById(String id);

    List<Tuser> findByIdIn(List<String> userIds);

    void insertUserRoleR(String id, String userId, String roleId);
}
