package com.szhq.iemp.device.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wanghao
 * @date 2019/10/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyName implements Serializable {

    private Integer code;

    private String name;
}
