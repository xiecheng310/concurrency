### 1. 生命周期
一个线程从创建然后start()执行到结束为一个生命周期.在这个生命周期中,线程对应了七种状态.分别是:
1. **init(start)**: 线程创建完成.执行start()方法
2. **ready-to-run**: 执行start()方法后.还未抢夺到cpu资源
3. **running**: 抢占到cpu资源(cpu为其分配了时间片)
4. **sleeping**: 执行Thread.sleep()方法
5. **waiting**: 执行Object.wait()方法
6. **blocked**: IO阻塞或者线程进入了synchronize代码块/方法
7. **dead**: 线程任务执行完成


### 2. wait notify/all 说明
1. wait()、notify/notifyAll() 方法是Object的本地final方法，无法被重写。
2. wait()使当前线程阻塞，前提是 必须先获得锁，一般配合synchronized 关键字使用，即，一般在synchronized 同步代码块里使用 wait()、notify/notifyAll() 方法。
3. 由于 wait()、notify/notifyAll() 在synchronized 代码块执行，说明当前线程一定是获取了锁的。
    - 当线程执行wait()方法时候，会释放当前的锁，然后让出CPU，进入等待状态。
    - 只有当 notify/notifyAll()被执行时候，才会唤醒一个或多个正处于等待状态的线程，然后继续往下执行，直到执行完synchronized 代码块的代码或是中途遇到wait() ，再次释放锁。
    - 也就是说，notify/notifyAll()的执行只是唤醒沉睡的线程，而不会立即释放锁，锁的释放要看代码块的具体执行情况。所以在编程中，尽量在使用了notify/notifyAll() 后立即退出临界区，以唤醒其他线程 
4. wait() 需要被try catch包围，中断也可以使wait等待的线程唤醒。
5. notify 和wait 的顺序不能错，如果A线程先执行notify方法，B线程在执行wait方法，那么B线程是无法被唤醒的。
6. notify 和 notifyAll的区别
    - notify方法只唤醒一个等待（对象的）线程并使该线程开始执行。所以如果有多个线程等待一个对象，这个方法只会唤醒其中一个线程，选择哪个线程取决于操作系统对多线程管理的实现。
    - notifyAll 会唤醒所有等待(对象的)线程，尽管哪一个线程将会第一个处理取决于操作系统的实现。如果当前情况下有多个线程需要被唤醒，推荐使用notifyAll 方法。比如在生产者-消费者里面的使用，每次都需要唤醒所有的消费者或是生产者，以判断程序是否可以继续往下执行。

### 3. 通过代码说明

```
package com.cgg.concurrency.lesson01;

import java.util.concurrent.TimeUnit;

/**
 * @author 
 */
public class Demo01_ThreadState {
    public static void main(String[] args) {
        // 线程初始化状态
        MyThread my = new MyThread();
        Thread thread = new Thread(my);
        // 就绪状态 (ready-to-run)
        thread.start();
        System.out.println("主线程...");
        // 超时等待 (sleep)
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 唤醒其他线程
        while (true) {
            synchronized (my){
                System.out.println("即将唤醒等待的线程...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                my.notify();
            }
        }

    }
}

/**
 * 自定义线程类
 */
class MyThread extends Thread {
    @Override
    public synchronized void run() {
        while (true) {
            System.out.println("自定义线程执行了...");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

```
