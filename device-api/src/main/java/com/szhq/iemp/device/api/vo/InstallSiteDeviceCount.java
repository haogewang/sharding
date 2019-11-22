package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InstallSiteDeviceCount implements Serializable {


    private static final long serialVersionUID = 1L;
    private Integer installSiteId;
    private String installSiteName;
    private Date date;

    private Long totalInstalledCount;
    private Long totalOnlineCount;
    private Double totalOnRate;

    private Long cmccInstalledCount;
    private Long cmccOnlineCount;
    private Double cmccOnRate;

    private Long ctInstalledCount;
    private Long ctOnlineCount;
    private Double ctOnRate;

    private Long cuInstalledCount;
    private Long cuOnlineCount;
    private Double cuOnRate;

}
