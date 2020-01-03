package com.szhq.iemp.reservation;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author wanghao
 * @date 2019/12/10
 */
public class Test3<E> {

    Deque<E> container =new ArrayDeque<E>();

    public void test(){
        Character.isLetter('a');
        container.pollLast();
    }
}
