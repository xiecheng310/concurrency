package com.cgg.concurrency.lesson07;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/13 22:38
 */
public class Demo01_Visibility {

    private int value = 1;

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }
}
