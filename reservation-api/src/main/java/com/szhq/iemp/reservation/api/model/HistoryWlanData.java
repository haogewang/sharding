package com.szhq.iemp.reservation.api.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Table(value = "history_wlan_data")
@lombok.Data
@NoArgsConstructor
public class HistoryWlanData implements Serializable {

    @PrimaryKey
    private NbiotTrackerDataPK trackerDataPK;

    private String location;

    private String version;

    private String wlan;

    @Column(value = "create_time")
    private String createTime;
}
