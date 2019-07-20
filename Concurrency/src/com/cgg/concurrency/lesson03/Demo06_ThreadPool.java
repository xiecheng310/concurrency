package com.cgg.concurrency.lesson03;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo06_ThreadPool {
    public static void main(String[] args) {
        // 1. 创建固定容量的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        // 2. 创建带缓冲区的线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        // 测试固定容量线程池,只有10个线程工作
        for (int i = 0; i < 20; i++) {
            fixedThreadPool.execute(() -> System.out.println("fix--" + Thread.currentThread().getName()));
        }
        // 测试带缓冲的线程池,容量不够回去申请新的线程
        for (int i = 0; i < 20; i++) {
            cachedThreadPool.execute(() -> System.out.println("cache--" + Thread.currentThread().getName()));
        }
        // 线程池需要手动关闭
        fixedThreadPool.shutdown();
        cachedThreadPool.shutdown();
    }
}