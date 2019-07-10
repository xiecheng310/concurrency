package com.cgg.concurrency.lesson06;

class SingletonHungry {

    private SingletonHungry() {
        // 私有构造方法,不能通过new创建对象
    }

    private SingletonHungry instance = new SingletonHungry(); // 每次加载类,实例化对象

    public SingletonHungry getInstance() {
        return instance;
    }
}