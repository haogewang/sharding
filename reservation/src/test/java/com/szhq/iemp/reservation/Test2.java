package com.szhq.iemp.reservation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wanghao
 * @date 2019/12/9
 */
public class Test2 {

    public static void main(String[] args) {
        char[] array = {'d', 'a', 'b', 'c', 'c', 'b'};
        Map<Object, Object> map = new HashMap<>();
        int index = -1;
        for (int i = array.length - 1; i >= 0; i--) {
            if (map.containsKey(array[i])) {
                int count = (Integer) map.get(array[i]);
                map.put(array[i], count + 1);
                index = i;
            } else {
                map.put(array[i], 1);
            }
        }
        System.out.println(index);
    }
}
