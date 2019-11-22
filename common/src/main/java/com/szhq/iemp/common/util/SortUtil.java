package com.szhq.iemp.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

public class SortUtil {

    /**
     * 排序
     * @param idName 主键属性名
     * @return
     */
    public static Sort sort(String sorts, String orders, String idName) {
        Sort sort = null;
        if (StringUtils.isEmpty(idName)) {
            return null;
        }
        if (StringUtils.isEmpty(sorts)) {
            sorts = idName;
        }
        List<Order> list = new ArrayList<>();
        if (!StringUtils.isBlank(orders) && "desc".equals(orders)) {
            sort = getOrders(sorts, list, Direction.DESC);
        }
        else if (!StringUtils.isBlank(orders) && "asc".equals(orders)) {
            sort = getOrders(sorts, list, Direction.ASC);
        }
        else {
            sort = getOrders(idName, list, Direction.DESC);
        }
        return sort;
    }

    private static Sort getOrders(String sorts, List<Order> list, Direction desc) {
        Sort sort;
        Order order1 = new Order(desc, "createTime");
        Order order2 = new Order(desc, sorts);
        list.add(order1);
        list.add(order2);
        sort = Sort.by(list);
        return sort;
    }

    public static Sort sort(String sorts, String orders) {
        Sort sort = null;
        List<Order> list = new ArrayList<>();
        if (!StringUtils.isBlank(orders) && "desc".equals(orders)) {
            Order order1 = new Order(Direction.DESC, sorts);
            list.add(order1);
            sort = Sort.by(list);
        }
        if (!StringUtils.isBlank(orders) && "asc".equals(orders)) {
            Order order1 = new Order(Direction.ASC, sorts);
            list.add(order1);
            sort = Sort.by(list);
        }
        return sort;
    }

    public static Sort sort(String sorts1, String sorts2,  Direction desc) {
        Sort sort = null;
        List<Order> list = new ArrayList<>();
        if (Direction.DESC.equals(desc)) {
            Order order1 = new Order(Direction.DESC, sorts1);
            Order order2 = new Order(Direction.DESC, sorts2);
            list.add(order1);
            list.add(order2);
            sort = Sort.by(list);
        }
        return sort;
    }

}
