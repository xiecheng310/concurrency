package com.cgg.concurrency.lesson06;

class SingletonLazy_V1 {

    private SingletonLazy_V1() {
        // 私有构造,不能new
    }

    private static SingletonLazy_V1 instance;

    public static SingletonLazy_V1 getInstance() {
        if (instance == null) {     // 这里出现了非原子性的操作
            instance = new SingletonLazy_V1();
        }
        return instance;
    }
}