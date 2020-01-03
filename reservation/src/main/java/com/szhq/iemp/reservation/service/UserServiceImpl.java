package com.szhq.iemp.reservation.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.constant.enums.exception.UserExceptionEnum;
import com.szhq.iemp.common.exception.NbiotException;
import com.szhq.iemp.common.util.PropertyUtil;
import com.szhq.iemp.reservation.api.model.Tuser;
import com.szhq.iemp.reservation.api.service.UserPushService;
import com.szhq.iemp.reservation.api.service.UserService;
import com.szhq.iemp.reservation.repository.UserRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    @Autowired
    private UserPushService userPushService;
    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;

    @Override
    public Tuser findByIdNumber(String idNumber) {
        return userRepository.findByIdNumber(idNumber);
    }

    @Override
    public Tuser findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public List<Tuser> findByOwnerName(String ownerName) {
        return userRepository.findByOwnerName(ownerName);
    }

    @Override
    public Tuser add(Tuser user) {
        if(StringUtils.isNotEmpty(user.getId())){
            redisUtil.del(CommonConstant.USER_ID + user.getId());
            redisUtil.del(CommonConstant.REGISTER_USERID + user.getId());
        }
        return userRepository.save(user);
    }

    @Override
    public Integer delete(String id) {
        int m = userRepository.deleteUserById(id);
        int i = userRepository.deleteUserRoleR(id);
        int j = userPushService.deleteByUserId(id);
        log.info("delete user count:{}, userrole count:{}, userPush count:{}", m, i, j);
        return 1;
    }

    @Override
    public Tuser updateUser(Tuser user) {
        if (StringUtils.isEmpty(user.getId())) {
            //UserExceptionEnum.E_0003
            throw new NbiotException(200001, "用户Id不能为空");
        }
        try {
            Tuser tuser = findById(user.getId());
            BeanUtils.copyProperties(user, tuser, PropertyUtil.getNullProperties(user));
            user = add(tuser);
        } catch (Exception e) {
            log.error("e", e);
            //UserExceptionEnum.E_0005
            throw new NbiotException(200003, "修改用户信息失败");
        }
        return user;
    }

    @Override
    public Tuser findById(String id) {
        String userString = (String) redisUtil.get(CommonConstant.USER_ID + id);
        if (StringUtils.isEmpty(userString)) {
            Tuser user = userRepository.findById(id).orElse(null);
            if (user != null) {
                redisUtil.set(CommonConstant.USER_ID + id, JSON.toJSONString(user));
            }
            return user;
        }
        log.info("get user data from redis.id:" + id);
        Tuser user = JSONObject.parseObject(userString, Tuser.class);
        return user;
    }

    @Override
    public List<Tuser> findByIdIn(List<String> userIds) {
        return userRepository.findByIdIn(userIds);
    }

    @Override
    public void insertUserRoleR(String id, String userId, String roleId) {
        userRepository.insertIntoUserRoleR(id, userId, roleId);
    }

}
