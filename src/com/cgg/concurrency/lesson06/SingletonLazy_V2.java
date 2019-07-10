package com.cgg.concurrency.lesson06;
public class SingletonLazy_V2 {
    private SingletonLazy_V2() {
        // 私有构造,不能new
    }
    private static SingletonLazy_V2 instance;

    public static synchronized SingletonLazy_V2 getInstance() {
        if (instance == null) {
            instance = new SingletonLazy_V2();
        }
        return instance;
    }
}