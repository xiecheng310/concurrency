package com.cgg.concurrency.lesson03;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class Demo04_WithReturnAndException {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyThread04 my = new MyThread04();
        FutureTask<Integer> task = new FutureTask<>(my);
        new Thread(task).start();
        Integer result = task.get();
        System.out.println("计算的结果是:" + result);
    }
}

class MyThread04 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("正在执行紧张的计算...");
        TimeUnit.SECONDS.sleep(1);
        return 1 + 1;
    }
}