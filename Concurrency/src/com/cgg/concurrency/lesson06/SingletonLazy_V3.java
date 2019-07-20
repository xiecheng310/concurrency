package com.cgg.concurrency.lesson06;
public class SingletonLazy_V3 {

    private SingletonLazy_V3() {
        // 私有构造,不能new
    }
    private static SingletonLazy_V3 instance;
    public static SingletonLazy_V3 getInstance() {
        if (instance == null) {
            synchronized (SingletonLazy_V3.class) {
                if (instance == null) {
                    instance = new SingletonLazy_V3();
                }
            }
        }
        return instance;
    }
}