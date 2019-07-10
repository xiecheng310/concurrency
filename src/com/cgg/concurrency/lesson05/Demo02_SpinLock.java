package com.cgg.concurrency.lesson05;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Demo02_SpinLock {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            begin();
        }

        while (Thread.activeCount() != 2) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println("我处于自旋等待状态...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("所有线程执行完成....");
    }

    private static void begin() {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "开始执行...");
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "执行完成...");
        }).start();
    }
}