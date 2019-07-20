### 1. 继承Thread类

```java
Thread thread = new Thread(...)
```

#### 1.1 线程的初始化
Thread类的构造方法有很多,但是都会执行init初始化方法

```java
/**
 * Initializes a Thread with the current AccessControlContext.
 * @see #init(ThreadGroup,Runnable,String,long,AccessControlContext,boolean)
 */
private void init(ThreadGroup g, Runnable target, String name, long stackSize) {
    init(g, target, name, stackSize, null, true);
}
```
参数说明:
- ThreadGroup: 线程组.对线程进行分组,树状结构,有线程组对应的api
- Runable: 线程任务
- name: 线程名字
- stackSize: 栈大小.并无实际意义,很多虚拟机都会忽略这个值

#### 1.2 守护线程
主线程结束后,还在执行任务的线程并不会停止.可以通过设置其他线程为守护线程,随着主线程的结束而结束

```java
package com.cgg.concurrence.lesson03.lesson03;

import java.util.concurrent.TimeUnit;

/**
 * @author 谢成
 */
public class Demo01_DaemonThread {
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyThread02());
        // 设置t1为守护线程
        t1.setDaemon(true);
        t1.start();
        // 主线程sleep 3s后结束
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程结束...");
    }
}

class MyThread01 extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName());
        }
    }
}
```
#### 1.3 线程的中断
JDK1.5之前,中断操作是通过stop()方法.但是因为这种方法不会让线程释放锁等资源.JDK6之后推荐使用interrupt()方法来中断线程.

```java
package com.cgg.concurrence.lesson03.lesson03;

import java.util.concurrent.TimeUnit;

public class Demo02_ThreadInterrupt {
    public static void main(String[] args) {
        Thread thread = new Thread(new MyThread02());
        thread.start();
        // 3s后中断线程
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}

class MyThread02 extends Thread {
    @Override
    public void run() {
        while (!interrupted()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(getName() + "...执行了");
        }
    }
}

```
### 2. 实现Runnable接口
实现接口其实是创建线程任务类,而非线程类.
#### 2.1 实现方式

```java
package com.cgg.concurrence.lesson03;

import java.util.concurrent.TimeUnit;

public class Demo03_ImplRunnable {
    public static void main(String[] args) {
        Thread thread = new Thread(new MyThread03());
        thread.start();
    }
}

class MyThread03 implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread running");
        }
    }
}
```
#### 2.2 源码解读
在创建Thread的时候,传入target,调用的构造方法如下:

```java
public Thread(Runnable target) {
    init(null, target, "Thread-" + nextThreadNum(), 0);
}
```
构造方法会去调用init方法,为成员变量target赋值

```java
this.group = g;
this.daemon = parent.isDaemon();
this.priority = parent.getPriority();
if (security == null || isCCLOverridden(parent.getClass()))
    this.contextClassLoader = parent.getContextClassLoader();
else
    this.contextClassLoader = parent.contextClassLoader;
this.inheritedAccessControlContext =
        acc != null ? acc : AccessController.getContext();
this.target = target;   // 在这里为成员变量赋值
setPriority(priority);
if (inheritThreadLocals && parent.inheritableThreadLocals != null)
    this.inheritableThreadLocals =
        ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
/* Stash the specified stack size in case the VM cares */
this.stackSize = stackSize;
```
start()后会执行Thread类的run()方法

```java
/**
 * If this thread was constructed using a separate
 * <code>Runnable</code> run object, then that
 * <code>Runnable</code> object's <code>run</code> method is called;
 * otherwise, this method does nothing and returns.
 * <p>
 * Subclasses of <code>Thread</code> should override this method.
 *
 * @see     #start()
 * @see     #stop()
 * @see     #Thread(ThreadGroup, Runnable, String)
 */
@Override
public void run() {
    if (target != null) {
        target.run();
    }
}
```
如果target为null. 我们就要重写Thread类的run()方法.否则没有线程任务执行.

#### 2.3 匿名内部类的写法
- 继承Thread类

```java
new Thread(){
    @Override
    public void run() {
        System.out.println("sub object ...");
    }
}.start();
```

- 实现Runnable接口

```java
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("runnable ...");
    }
}).start();
```

- 思考:同时使用?

```java
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("runnable ... ");
    }
}){
    @Override
    public void run() {
        System.out.println("sub object ...");
    }
}.start();
```
输出结果为sub object ... ,因为从源码可以看出, 如果重写了Thread的run方法, 会覆盖之前的方法.从而忽略掉target.

#### 3. 带返回值和抛出异常
- 线程任务类实现callable接口,泛型类型为返回值的类型.
- 线程类使用FutureTask.该接口继承了Runnable,可以直接作为target.

```java
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
```
### 4. 通过定时器创建线程

```java
package com.cgg.concurrency.lesson03;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Demo05_Timer {
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date()));
            }
        }, 0, 1000);    // 每秒执行一次

    }
}
```
### 5. 线程池

```java
package com.cgg.concurrency.lesson03;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo06_ThreadPool {
    public static void main(String[] args) {
        // 1. 创建固定容量的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        // 2. 创建带缓冲区的线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        // 测试固定容量线程池,只有10个线程工作
        for (int i = 0; i < 20; i++) {
            fixedThreadPool.execute(() -> System.out.println("fix--" + Thread.currentThread().getName()));
        }
        // 测试带缓冲的线程池,容量不够回去申请新的线程
        for (int i = 0; i < 20; i++) {
            cachedThreadPool.execute(() -> System.out.println("cache--" + Thread.currentThread().getName()));
        }
        // 线程池需要手动关闭
        fixedThreadPool.shutdown();
        cachedThreadPool.shutdown();
    }
}
```

### 6. lambda中的并发流parallel stream

```
package com.cgg.concurrency.lesson03;

import java.util.Arrays;
import java.util.List;

public class Demo07_ParallelStream {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println(add(list));
    }
    private static int add(List<Integer> list){
        list.parallelStream().forEach(System.out::println);
        return list.parallelStream().mapToInt(value -> value).sum();
    }
}
```
输出结果是无序的.因此是一个并发操作




