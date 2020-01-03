package com.szhq.iemp.reservation.service;

import com.szhq.iemp.common.vo.MyPage;
import com.szhq.iemp.reservation.api.model.*;
import com.szhq.iemp.reservation.api.service.*;
import com.szhq.iemp.reservation.api.vo.query.AlarmQuery;
import com.szhq.iemp.reservation.repository.EsNbiotDeviceAlarmRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class EsNbiotDeviceAlarmServiceImpl implements EsNbiotDeviceAlarmService {

    @Resource
    private EsNbiotDeviceAlarmRepository esNbiotDeviceAlarmRepository;
    @Autowired
    private ElectrmobileService electrombileService;
    @Autowired
    private UserService userService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    @Qualifier("transportClient")
    private TransportClient transportClient;

    @Override
    public MyPage<EsNbiotDeviceAlarm> alarmList(Integer page, Integer size, String sorts, String orders, AlarmQuery alarmQuery) {
        Sort sort = sort(sorts, orders);
        Pageable pageable = PageRequest.of(page, size, sort);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("ts");
        if (alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getImei())) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("imei", alarmQuery.getImei()));
        }
        if (alarmQuery != null && alarmQuery.getType() != null) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("type", alarmQuery.getType()));
        }
        if (alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getModelNo())) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("devType", alarmQuery.getModelNo()));
        }
        if (alarmQuery != null && alarmQuery.getOperatorIdList() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("operator", alarmQuery.getOperatorIdList()));
        }
        if (alarmQuery != null && alarmQuery.getImeiList() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("imei", alarmQuery.getImeiList()));
        }
//        boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery("type", 6));
//        boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery("type", 7));
        if (alarmQuery != null && alarmQuery.getStartTimestamp() != null && alarmQuery.getEndTimestamp() != null) {
            rangeQueryBuilder.lte(alarmQuery.getEndTimestamp());
            rangeQueryBuilder.gte(alarmQuery.getStartTimestamp());
            boolQueryBuilder.must(rangeQueryBuilder);
        }
        if (alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getPlateNo())) {
            Telectrmobile electrombile = electrombileService.findByPlateNumber(alarmQuery.getPlateNo());
            if (electrombile == null) {
                log.error("plateNo: " + alarmQuery.getPlateNo() + " is not exist!");
                return null;
            }
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("imei", electrombile.getImei()));
        }
        if (alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getPhone())) {
            Tuser user = userService.findByPhone(alarmQuery.getPhone());
            if (user == null) {
                log.error("phone: " + alarmQuery.getPhone() + " is not exist!");
                return null;
            }
            List<Telectrmobile> elecs = new ArrayList<>();
            if (StringUtils.isNotEmpty(alarmQuery.getModelNo())) {
                elecs = electrombileService.getAllElecmobileByUserIdAndType(user.getId(), alarmQuery.getModelNo());
            } else {
                elecs = electrombileService.getAllElecmobileByUserId(user.getId());
            }
            List<String> imeis = new ArrayList<>();
            for (Telectrmobile elec : elecs) {
                String imei = elec.getImei();
                imeis.add(imei);
            }
            boolQueryBuilder.must(QueryBuilders.termsQuery("imei", imeis));
        }
        if (alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getOwnerName())) {
            List<String> imeis = registrationService.findByUserNameLikeAndImeiIsNotNUll(alarmQuery.getOwnerName());
            if(imeis != null && !imeis.isEmpty()){
                boolQueryBuilder.must(QueryBuilders.termsQuery("imei", imeis));
            }
        }
        if(alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getOwnerId())){
            List<String> imeis = registrationService.findByUserIdAndImeiIsNotNUll(alarmQuery.getOwnerId());
            if(imeis != null && !imeis.isEmpty()){
                boolQueryBuilder.must(QueryBuilders.termsQuery("imei", imeis));
            }
        }
        if(alarmQuery != null && alarmQuery.getEmergencyAlert() != null){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("emergencyAlert", alarmQuery.getEmergencyAlert()));
        }
        SearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
        searchQuery.setPageable(pageable);
        log.info("search query:" + boolQueryBuilder);
        Page<EsNbiotDeviceAlarm> pages = esNbiotDeviceAlarmRepository.search(searchQuery);
        return new MyPage<EsNbiotDeviceAlarm>(pages.getContent(), pages.getTotalElements(), pages.getNumber(), pages.getSize());
    }

    @Override
    public Map<Long, Long> alarmStastic(AlarmQuery alarmQuery) {
        Map<Long, Long> map = new HashMap<Long, Long>();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("ts");
        if (alarmQuery != null && alarmQuery.getStartTimestamp() != null && alarmQuery.getEndTimestamp() != null) {
            rangeQueryBuilder.gte(alarmQuery.getStartTimestamp());
            rangeQueryBuilder.lte(alarmQuery.getEndTimestamp());
            boolQueryBuilder.must(rangeQueryBuilder);
        }
        if (alarmQuery != null && alarmQuery.getOperatorIdList() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("operator", alarmQuery.getOperatorIdList()));
        }
        SearchRequestBuilder requestBuilder = transportClient.prepareSearch("history-alarm-data").setTypes("alarm").setQuery(boolQueryBuilder);
        TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("typeAgg").field("type");
        requestBuilder.addAggregation(termsBuilder);
        SearchResponse response = requestBuilder.execute().actionGet();
        //得到这个分组的集合
        Map<String, Aggregation> aggMap = response.getAggregations().asMap();
        LongTerms typeTerms = (LongTerms) aggMap.get("typeAgg");
        Iterator<LongTerms.Bucket> typeBucketIt = typeTerms.getBuckets().iterator();
        while (typeBucketIt.hasNext()) {
            Bucket typeBucket = typeBucketIt.next();
            log.info(typeBucket.getKey() + "-" + typeBucket.getDocCount());
            map.put((Long) typeBucket.getKey(), typeBucket.getDocCount());
        }
        return map;
    }

    @Override
    public Map<String, Long> elecAlarmSort(AlarmQuery alarmQuery) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        Map<String, Long> map = new HashMap<String, Long>();
        if (alarmQuery != null && alarmQuery.getOperatorIdList() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("operator", alarmQuery.getOperatorIdList()));
        }
        SearchRequestBuilder requestBuilder = transportClient.prepareSearch("history-alarm-data").setTypes("alarm").setQuery(boolQueryBuilder);
        TermsAggregationBuilder termsBuilder = AggregationBuilders.terms("imeiAgg").field("imei.keyword").order(BucketOrder.count(false)).size(5);
        requestBuilder.addAggregation(termsBuilder);
        SearchResponse response = requestBuilder.execute().actionGet();
        //得到这个分组的集合
        Map<String, Aggregation> aggMap = response.getAggregations().asMap();
        StringTerms typeTerms = (StringTerms) aggMap.get("imeiAgg");
        Iterator<StringTerms.Bucket> typeBucketIt = typeTerms.getBuckets().iterator();
        while (typeBucketIt.hasNext()) {
            Bucket typeBucket = typeBucketIt.next();
            log.info(typeBucket.getKey() + "-" + typeBucket.getDocCount());
            map.put((String) typeBucket.getKey(), typeBucket.getDocCount());
        }
        return map;
    }

    @Override
    public Long elecAlarmCount(AlarmQuery alarmQuery) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("ts");
        if (alarmQuery != null && StringUtils.isNotEmpty(alarmQuery.getImei())) {
            log.debug("");
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("imei", alarmQuery.getImei()));
        }
        if (alarmQuery != null && alarmQuery.getStartTimestamp() != null) {
            rangeQueryBuilder.gte(alarmQuery.getStartTimestamp());
            boolQueryBuilder.must(rangeQueryBuilder);
        }
        SearchRequestBuilder requestBuilder = transportClient.prepareSearch("history-alarm-data").setTypes("alarm").setQuery(boolQueryBuilder);
        ValueCountAggregationBuilder valueCountAgg = AggregationBuilders.count("countAgg").field("imei.keyword");
        requestBuilder.addAggregation(valueCountAgg);
        SearchResponse response = requestBuilder.execute().actionGet();
        //得到这个分组的集合
        Map<String, Aggregation> aggMap = response.getAggregations().asMap();
        InternalValueCount countTerms = (InternalValueCount) aggMap.get("countAgg");
        Long count = countTerms.getValue();
        return count;
    }


    @Override
    public Long deleteByImei(String imei) {
        long count = 0;
        Pageable pageable = PageRequest.of(0, 3000);
        QueryBuilder queryBuilder = new MatchQueryBuilder("imei", imei);
        while(true){
            Page<EsNbiotDeviceAlarm> catIterable = esNbiotDeviceAlarmRepository.search(queryBuilder, pageable);
            if (catIterable != null && catIterable.getTotalElements() > 0) {
                esNbiotDeviceAlarmRepository.deleteAll(catIterable);
                count += catIterable.getTotalElements();
            }else{
                break;
            }
        }
        return count;
    }


    public String alarmStatistic(Integer offset) {
        QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(matchAllQuery);
        nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);
        nativeSearchQueryBuilder.withIndices("history-alarm-data").withTypes("alarm");
        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("alarm-groupby-type").field("type");
        ValueCountAggregationBuilder vcb = AggregationBuilders.count("alarm-count").field("imei.keyword");
        //		AggregationBuilders.dateHistogram("").field("").dateHistogramInterval(DateHistogramInterval.days(2));
        nativeSearchQueryBuilder.addAggregation(termsAggregation.subAggregation(vcb));
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        log.info("search Aggregate query:" + nativeSearchQueryBuilder.toString());
        Iterable<EsNbiotDeviceAlarm> pages = esNbiotDeviceAlarmRepository.search(nativeSearchQuery);
        while (pages != null && pages.iterator().hasNext()) {
            EsNbiotDeviceAlarm alarm = pages.iterator().next();
            log.info("alarm:" + alarm);
        }
        return null;
    }

    private Sort sort(String sorts, String orders) {
        Sort sort = null;
        if (StringUtils.isEmpty(sorts)) {
            sorts = "ts";// ".keyword"
        }
        List<Order> list = new ArrayList<>();
        if (!StringUtils.isBlank(orders) && "desc".equals(orders)) {
            Order order = new Order(Direction.DESC, sorts);
            list.add(order);
            sort = Sort.by(list);
        } else if (!StringUtils.isBlank(orders) && "asc".equals(orders)) {
            Order order = new Order(Direction.ASC, sorts);
            list.add(order);
            sort = Sort.by(list);
        } else {
            Order order = new Order(Direction.DESC, "ts");
            list.add(order);
            sort = Sort.by(list);
        }
        return sort;
    }

}
