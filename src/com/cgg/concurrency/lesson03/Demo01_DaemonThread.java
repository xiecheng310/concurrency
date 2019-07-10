package com.cgg.concurrency.lesson03;

import java.util.concurrent.TimeUnit;

/**
 * @author 谢成
 */
public class Demo01_DaemonThread {
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyThread01());
        // 设置t1为守护线程
        t1.setDaemon(true);
        t1.start();
        // 主线程sleep 3s后结束
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程结束...");
    }
}

class MyThread01 extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName());
        }
    }
}