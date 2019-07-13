package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:13
 */
public class Demo02_atomicArray {
    private int[] arr = {1, 2, 3};
    private AtomicIntegerArray aia = new AtomicIntegerArray(arr);

    private int getArrNext() {
        int add = aia.getAndAdd(1, 10);//索引1的元素+10
        int increment = aia.getAndIncrement(1);// 索引1的元素自增1
        return add;
    }
}
