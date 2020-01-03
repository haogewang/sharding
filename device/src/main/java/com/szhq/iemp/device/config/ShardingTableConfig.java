package com.szhq.iemp.device.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
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
//                if (tableName.endsWith(Long.valueOf(shardingValue.getValue()) % 4 + "")) {
//                    log.info("tablename:" + tableName);
//                    return tableName;
//                }
            } else {
                log.error(tableName + "分表字段：imei缺失");
                throw new IllegalArgumentException(tableName + " 分表字段:imei缺失");
            }
        }
        return null;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> shardingValue) {
        log.info("RangeShard-availableTargetNames:" + JSONObject.toJSONString(availableTargetNames));
        log.info("RangeShard-shardingValue:" + JSONObject.toJSONString(shardingValue));
        String tableNameB = "role0";
        Collection<String> tables = new HashSet<>();
//        if (availableTargetNames.contains(tableNameB)) {
//            if ("imei".equalsIgnoreCase(shardingValue.getColumnName())) {
//                Collection<String> collect = new ArrayList<>();
//                Range<String> valueRange = shardingValue.getValueRange();
//                for (Long i = valueRange.lowerEndpoint(); i <= valueRange.upperEndpoint(); i++) {
//                    for (String each : availableTargetNames) {
//                        if (each.endsWith(i % availableTargetNames.size() + "")) {
//                            collect.add(each);
//                        }
//                    }
//                }
//                log.info("availableTargetNames:" + JSONObject.toJSONString(collect));
                return availableTargetNames;
//            }
//            throw new IllegalArgumentException("分表字段：id缺失");
        }
//        return null;
//    }
}
