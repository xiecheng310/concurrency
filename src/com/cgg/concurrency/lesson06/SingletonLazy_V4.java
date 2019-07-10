package com.cgg.concurrency.lesson06;

public class SingletonLazy_V4 {
    private SingletonLazy_V4() {
        // 私有构造,不能new
    }
    private static volatile SingletonLazy_V4 instance;  // volatile禁止指令重排序

    public static SingletonLazy_V4 getInstance() {
        if (instance == null) {
            synchronized (SingletonLazy_V4.class) {
                if (instance == null) {
                    instance = new SingletonLazy_V4();
                }
            }
        }
        return instance;
    }
}