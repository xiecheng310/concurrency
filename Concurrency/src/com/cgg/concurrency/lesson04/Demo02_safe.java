package com.cgg.concurrency.lesson04;

import java.util.concurrent.TimeUnit;

public class Demo02_safe {

    private int value;

    private int getNext(){
        return value ++ ;
    }

    public static void main(String[] args) {
        Demo02_safe safe = new Demo02_safe();
        getNextValue(safe);
        getNextValue(safe);
        getNextValue(safe);
    }

    private static void getNextValue(Demo02_safe safe) {
        new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "..." + safe.getNext());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}