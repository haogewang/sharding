package com.szhq.iemp.reservation.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashSet;

/**
 * 数据库分表
 * @author wanghao
 * @date 2019/10/14
 */
@Configuration
@Slf4j
public class ShardingTableConfig implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<String> {

    /**
     * 精确分片算法,用于=和IN
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
//        log.info("PreciseShard-availableTargetNames:" + JSONObject.toJSONString(availableTargetNames));
//        log.info("PreciseShard-shardingValue:" + JSONObject.toJSONString(shardingValue));
        for (String tableName : availableTargetNames) {
            if ("imei".equalsIgnoreCase(shardingValue.getColumnName())) {
                return tableName;
            } else {
                log.error(tableName + "分表字段：imei 缺失");
                throw new IllegalArgumentException(tableName + "分表字段:imei缺失");
            }
        }
        return null;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> shardingValue) {
        log.info("RangeShard-availableTargetNames:" + JSONObject.toJSONString(availableTargetNames));
        log.info("RangeShard-shardingValue:" + JSONObject.toJSONString(shardingValue));
        Collection<String> tables = new HashSet<>();
                return availableTargetNames;
        }
}
