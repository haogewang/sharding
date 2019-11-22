package com.szhq.iemp.reservation.config;

import com.alibaba.fastjson.JSONObject;
import com.szhq.iemp.common.exception.NbiotException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
@Slf4j
public class EsConfig {

    /**
     * es集群地址
     */
    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String nodes;
    /**
     * 集群名称
     */
    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;


    @Bean(name = "transportClient")
    public TransportClient transportClient() {
        log.info("ES config start...");
        TransportClient transportClient = null;
        try {
            // 配置信息
            Settings esSetting = Settings.builder()
                    .put("cluster.name", clusterName) //集群名字
                    .put("client.transport.sniff", true)//增加嗅探机制，找到ES集群
                    .put("thread_pool.search.size", 5)//增加线程池个数，暂时设为5
                    .build();
            //配置信息Settings自定义
            transportClient = new PreBuiltTransportClient(esSetting);
            String[] nodeArr = nodes.split(",");
            if (nodeArr != null && nodeArr.length > 0) {
                for (int i = 0; i < nodeArr.length; i++) {
                    TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(nodeArr[i].split(":")[0]), Integer.valueOf(nodeArr[i].split(":")[1]));
                    transportClient.addTransportAddresses(transportAddress);
                }
            } else {
                throw new NbiotException(500, "no ES config find");
            }
        } catch (Exception e) {
            log.error("elasticsearch TransportClient create error!!", e);
        }
        log.info("ES config finished...");
        if (transportClient != null && transportClient.listedNodes() != null) {
            for (DiscoveryNode node : transportClient.listedNodes()) {
                log.info("es nodes:" + node.getAddress().getAddress() + ":" + node.getAddress().getPort());
            }
        }
        return transportClient;
    }

}
