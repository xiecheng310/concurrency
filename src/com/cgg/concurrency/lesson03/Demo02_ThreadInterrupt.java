package com.cgg.concurrency.lesson03;

import java.util.concurrent.TimeUnit;

public class Demo02_ThreadInterrupt {
    public static void main(String[] args) {
        Thread thread = new Thread(new MyThread02());
        thread.start();
        // 3s后中断线程
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}

class MyThread02 extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getName() + "...执行了");
        }
    }
}