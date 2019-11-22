package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.BaseQuery;
import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.TdeviceInventory;
import com.szhq.iemp.device.api.model.Tgroup;
import com.szhq.iemp.device.api.model.TiotType;
import com.szhq.iemp.device.api.vo.UserAndElecInfo;
import com.szhq.iemp.device.api.vo.query.GroupQuery;

import java.security.acl.Group;
import java.util.List;
import java.util.Map;

public interface GroupService {
    /**
     * 列表
     */
    MyPage<Tgroup> findGroupByCriteria(Integer page, Integer size, String sorts, String orders, GroupQuery myQuery);

    /**
     * 获取所有设备分组子类
     */
    List<Tgroup> getAllDeviceGroupChildrenById(Integer id);
    /**
     * 获取所有车辆分组子类
     */
    List<Tgroup> getAllElecGroupChildrenById(Integer id);
    /**
     * 根据imei查找用户及车信息
     */
    List<UserAndElecInfo> getUserAndElecInfoByImeis(List<String> imeis);

    /**
     * 获取下级车辆分组
     */
    List<Tgroup> getNextElecGroupById(Integer id);
    /**
     * 获取下级设备分组
     */
    List<Tgroup> getNextDeviceGroupById(Integer id);

    /**
     * 添加
     */
    Tgroup save(Tgroup entity);

    /**
     * 删除设备分组
     */
    Integer deleteDeviceGroupById(Integer id);
    /**
     * 删除车辆分组
     */
    Integer deleteElecGroupById(Integer id);

    /**
     * 通过名字查找
     */
    Tgroup findByName(String name);

    /**
     * 通过id查找
     */
    Tgroup findById(Integer id);

    /**
     * 分配设备到设备组
     */
    Integer dispatchToDeviceGroup(List<String> imeis, Integer groupId);

    /**
     * 分配车到车辆组
     */
    Integer dispatchToElecGroup(List<String> imeis, Integer groupId);
    /**
     *移除设备分组
     */
    Integer removeDeviceGroup(List<String> imeis);
    /**
     *移除设备分组
     */
    Integer removeElecGroup(List<String> imeis);

}
