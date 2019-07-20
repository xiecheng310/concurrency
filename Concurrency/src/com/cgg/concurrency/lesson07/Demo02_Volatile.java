package com.cgg.concurrency.lesson07;


/**
 * @author Donavon Xie
 * CREATE AT 2019/7/13 22:48
 */
public class Demo02_Volatile {

    private volatile boolean run = false;

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public static void main(String[] args) {

        Demo02_Volatile demo = new Demo02_Volatile();

        new Thread(() ->{
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "执行第" + i + "次");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            demo.setRun(true);
        }).start();

        new Thread(() -> {
            String name = Thread.currentThread().getName();
            while (!demo.isRun()){
                // 自旋等待
                try {
                    Thread.sleep(200);
                    System.out.println(name + "等在run为true...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(name + "开始执行");
        }).start();
    }
}