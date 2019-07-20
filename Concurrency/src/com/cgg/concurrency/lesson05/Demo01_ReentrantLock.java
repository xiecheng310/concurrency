package com.cgg.concurrency.lesson05;

public class Demo01_ReentrantLock {

    public static void main(String[] args) {
        Demo01_ReentrantLock r = new Demo01_ReentrantLock();
        r.fun1();
    }

    public synchronized void fun1() {
        System.out.println("I'm fun1()");
        fun2(); // 锁重入， 两个同步方法使用同一把锁
    }

    private synchronized void fun2() {
        System.out.println("I'm fun2()");
    }
}