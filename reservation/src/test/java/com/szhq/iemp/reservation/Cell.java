package com.szhq.iemp.reservation;

import lombok.Data;

import java.util.List;

/**
 * @author wanghao
 * @date 2019/12/5
 */
@Data
public class Cell {

    private String name;
    private List<String> neibers;
}
