package com.cgg.concurrency.lesson03;

import java.util.concurrent.TimeUnit;

public class Demo03_ImplRunnable {
    public static void main(String[] args) {
        Thread thread = new Thread(new MyThread03());
        thread.start();
    }
}

class MyThread03 implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread running");
        }
    }
}