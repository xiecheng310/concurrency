package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:20
 */
public class Demo04_atomicObjectProperty {
    private User init = new User("tom", 10);
    // age字段必须要volatile修饰，且不能为private
    private static final AtomicIntegerFieldUpdater<User> user =
            AtomicIntegerFieldUpdater.newUpdater(User.class, "age");

    public int getUserAge() {
        return user.getAndIncrement(init);
    }

    public static void main(String[] args) {
        Demo04_atomicObjectProperty demo = new Demo04_atomicObjectProperty();
        getNextAge(demo);
        getNextAge(demo);
        getNextAge(demo);

    }

    private static void getNextAge(Demo04_atomicObjectProperty demo) {
        new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "..." + demo.getUserAge());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
