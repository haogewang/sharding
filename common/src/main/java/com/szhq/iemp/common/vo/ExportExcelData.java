package com.szhq.iemp.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 列表批量导出数据
 * @author wanghao
 */
@Data
public class ExportExcelData implements Serializable {

    private static final long serialVersionUID = 1L;

    // 表头
    private List<String> titles;

    // 数据
    private List<List<String>> rows;

    // 页签名称
    private String name;

}
