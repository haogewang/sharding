package com.szhq.iemp.reservation.service;


import com.szhq.iemp.common.constant.CommonConstant;
import com.szhq.iemp.common.util.ListTranscoder;
import com.szhq.iemp.reservation.api.model.Toperator;
import com.szhq.iemp.reservation.api.service.OperatorService;
import com.szhq.iemp.reservation.repository.OperatorRepository;
import com.szhq.iemp.reservation.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OperatorServiceImpl implements OperatorService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorServiceImpl.class);

    @Resource
    private OperatorRepository operatorRepository;

    @Resource(name = "primaryRedisUtil")
    private RedisUtil redisUtil;


    @Override
    public List<Integer> findAllChildIds(Integer parentId) {
        Object datas = redisUtil.get(CommonConstant.OPERATOR_REDISKEY + parentId);
        ListTranscoder<Integer> listTranscoder = new ListTranscoder<Integer>();
        if (datas == null) {
            List<Integer> ids = new ArrayList<>();
            findById(parentId, ids);
            ids.add(parentId);
            if (ids.size() > 0) {
                redisUtil.set(CommonConstant.OPERATOR_REDISKEY + parentId, listTranscoder.serialize(ids));
            }
            return ids;
        }
        Object o = listTranscoder.deserialize((String) datas);
        logger.info("get operators data from redis.id:" + parentId);
        return (List<Integer>) o;
    }

    private void findById(Integer parentId, List<Integer> ids) {
        List<Map<String, Object>> lists = operatorRepository.findAllChildrenById(parentId);
        if (lists != null && lists.size() > 0) {
            for (Map<String, Object> map : lists) {
                long id = Long.valueOf(String.valueOf(map.get("id")));
                logger.debug("");
                int oId = Integer.valueOf(String.valueOf(id));
                ids.add(oId);
                findById(oId, ids);
            }
        }
    }

    @Override
    public Toperator findById(Integer id) {
        return operatorRepository.findById(id).orElse(null);
    }


}
