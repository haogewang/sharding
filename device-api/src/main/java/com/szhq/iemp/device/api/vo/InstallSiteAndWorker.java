package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * @author wanghao
 * @date 2019/9/2
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InstallSiteAndWorker {

    private String workerId;

    private String workerName;

    private String workerLoginName;

    private String workerPhone;

    private String installSiteName;

    private Integer installSiteId;

    private Double avgScore;

    private Integer totalCount;

    private Integer unNormalCount;

    private String date;
}
