package com.szhq.iemp.reservation.repository;


import com.szhq.iemp.reservation.api.model.EsTrackerTravel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsTrackvelRepostiory extends ElasticsearchRepository<EsTrackerTravel, Long> {
}
