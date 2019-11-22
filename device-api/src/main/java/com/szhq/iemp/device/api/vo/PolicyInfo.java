package com.szhq.iemp.device.api.vo;

import com.szhq.iemp.device.api.model.TpolicyInfo;
import com.szhq.iemp.device.api.model.Telectrmobile;
import com.szhq.iemp.device.api.model.Tuser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *保单信息
 * @author wanghao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyInfo extends TpolicyInfo {

    private static final long serialVersionUID = 1L;

    private Tuser user;

    private Telectrmobile electrombile;
    //保险ids
    private List<Integer> insuranceIds;
    //以年为单位
    @NotNull(message = "保险期限不能为空")
    @Min(value = 1, message = "最小为1年")
    private Integer period;

}
