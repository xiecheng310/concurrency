package com.cgg.concurrency.lesson06;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckThreadSafe {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            service.execute(() ->
                System.out.println(Thread.currentThread().getName() + SingletonLazy_V2.getInstance())
            );
        }
        service.shutdown();
    }
}