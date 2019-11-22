package com.szhq.iemp.device.api.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.device.api.model.Toperator;
import com.szhq.iemp.device.api.vo.OperatorVo;
import com.szhq.iemp.device.api.vo.query.OperatorQuery;

import java.util.List;

public interface OperatorService {
    /**
     * 运营公司列表
     */
    MyPage<Toperator> findAllByCriteria(Integer page, Integer size, String sorts, String orders, OperatorQuery myQuery);

    /**
     * 找到所有子运营公司的信息
     */
    List<Toperator> findAllChildrenInfo(Integer id);

    /**
     *查找所有运营公司
     */
    List<Toperator> findAll();
    /**
     * 通过仓库Id找运营公司Id
     */
    Integer findOperatorIdByStoreHouseId(Integer storehouseId);
    /**
     * 添加(创建仓库)
     */
    Toperator add(Toperator entity);
    /**
     * 添加运营公司(不创建仓库)
     */
    Toperator save(Toperator entity);
    /**
     * 修改
     */
    Toperator update(Toperator entity);
    /**
     * 删除
     */
    Integer delete(Integer id);
    /**
     * 通过名称查找
     */
    Toperator findByName(String name);
    /**
     * 通过id查找
     */
    Toperator findById(Integer id);
    /**
     * 递归找到所有子类Id
     */
    List<Integer> findAllChildIds(Integer parentId);
    /**
     * 删除redis缓存
     */
    Integer deleteRedisData(Integer parentId);

    List<Toperator> findByParent(Integer id);

    /**
     * 找到需要入库的运营公司
     */
    List<Toperator> findNeedPutStorageOperators();

    List<Toperator> findByParentIdAndStoreIsActive(Integer id, Boolean isActive);
}
