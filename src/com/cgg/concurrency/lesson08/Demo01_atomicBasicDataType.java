package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo01_atomicBasicDataType {

    private AtomicInteger value = new AtomicInteger(0);


    private int getNext(){
        return value.getAndIncrement() ;
    }

    public static void main(String[] args) {
        Demo01_atomicBasicDataType unsafe = new Demo01_atomicBasicDataType();
        getNextValue(unsafe);
        getNextValue(unsafe);
        getNextValue(unsafe);
    }

    private static void getNextValue(Demo01_atomicBasicDataType unsafe) {
        new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "..." + unsafe.getNext());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}