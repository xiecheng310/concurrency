package com.cgg.concurrency.lesson02;

import java.util.concurrent.TimeUnit;

/**
 * @author 谢成
 */
public class Demo01_ThreadState {
    public static void main(String[] args) {
        // 线程初始化状态
        MyThread my = new MyThread();
        Thread thread = new Thread(my);
        // 就绪状态 (ready-to-run)
        thread.start();
        System.out.println("主线程...");
        // 超时等待 (sleep)
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 唤醒其他线程
        while (true) {
            synchronized (my){
                System.out.println("即将唤醒等待的线程...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                my.notify();
            }
        }

    }
}

/**
 * 自定义线程类
 */
class MyThread extends Thread {
    @Override
    public synchronized void run() {
        while (true) {
            System.out.println("自定义线程执行了...");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}