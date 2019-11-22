package com.szhq.iemp.device.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wanghao
 * @date 2019/8/5
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OperatorVo implements Serializable {

    private Integer id;
    private Integer pid;
    private String name;
    private String adressName;
    private List<OperatorVo> children;
}
