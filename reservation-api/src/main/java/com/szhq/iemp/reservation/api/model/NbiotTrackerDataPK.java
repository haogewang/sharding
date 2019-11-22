package com.szhq.iemp.reservation.api.model;

import lombok.AllArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Date;

@PrimaryKeyClass
@lombok.Data
@AllArgsConstructor
public class NbiotTrackerDataPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "imei", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String imei;

    @PrimaryKeyColumn(name = "ts", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date ts;
}
