package com.cgg.concurrency.lesson04;

public class Demo01_hungry {
    public static void main(String[] args) {
        Thread t1 = new Thread(new Target01A());
        Thread t2 = new Thread(new Target01B());

        // 优先级范围从1-10(windows)
        t1.setPriority(Thread.MIN_PRIORITY);
        t2.setPriority(Thread.MAX_PRIORITY);

        t1.start();
        t2.start();

    }
}

class Target01A implements Runnable{
    private int a =0;
    @Override
    public void run() {
        while (true) {
            System.out.println("a ----" + a ++);
        }
    }
}

class Target01B implements Runnable{
    private int b = 0;
    @Override
    public void run() {
        while (true) {
            System.out.println("b ----" + b ++);
        }
    }
}