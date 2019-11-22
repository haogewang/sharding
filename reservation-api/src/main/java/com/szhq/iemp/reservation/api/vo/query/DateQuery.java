package com.szhq.iemp.reservation.api.vo.query;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
public class DateQuery implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long startTime;
    private Long endTime;
}
