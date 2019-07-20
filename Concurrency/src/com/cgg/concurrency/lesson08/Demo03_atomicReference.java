package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:15
 */
public class Demo03_atomicReference {
    private AtomicReference<User> user;

    public User getUser() {
        return user.getAndSet(new User("tom", 10));
    }
}
