package com.cgg.concurrency.lesson08;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:14
 */
public class User {
    private String name;
    volatile int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
