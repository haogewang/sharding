package com.szhq.iemp.reservation.service;

import com.szhq.iemp.reservation.api.model.EsTrackerTravel;
import com.szhq.iemp.reservation.repository.EsTrackvelRepostiory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
public class EsTrackvelServiceImpl {

	@Autowired
	private EsTrackvelRepostiory elasticsearchRepostiory;

	public void deleteByImei(String Imei){
		QueryBuilder queryBuilder  = new MatchQueryBuilder("imei", Imei);
		Iterable<EsTrackerTravel> catIterable = elasticsearchRepostiory.search(queryBuilder);
		if(catIterable != null)
			elasticsearchRepostiory.deleteAll(catIterable);
		return;
	}


}
