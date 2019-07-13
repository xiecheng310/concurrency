### 1. volatile关键字含义
volatile 称之为一个轻量级锁, 被volatile修饰的变量,在线程之间是可见的.一个线程修改了这个变量, 在另外一个线程中,能够读取到这个修改后的值.
synchronized除了能让线程之间互斥以外,也能保证可见性.

#### 1.1 什么是可见性？
一个线程修改了对象的状态后，其他线程能够看到发生的状态变化。比如：
```java
package com.cgg.concurrency.lesson07;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/13 22:38
 */
public class Demo01_Visibility {
    
    private int value = 1;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
```
多个线程在对成员变量进行读写操作的时候，可能会出现线程安全问题，解决办法之一就是让get/set方法保证同步:
```java
package com.cgg.concurrency.lesson07;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/13 22:38
 */
public class Demo01_Visibility {
    
    private int value = 1;

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }
}
```
但前提是要让多个线程获取的同一把锁。这种方式虽然可以解决线程安全问题，但是太重。、
#### 1.2 volatile使用场景
- 需求：当一个线程执行完毕后,另一个线程才开始执行. 通过成员变量boolean值控制.
```java
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
```

### 2. volatile底层实现
添加volatile关键字,在汇编层面,会多一个Lock指令.即在多处理器的系统上,将当前处理器缓存行的内容写回到系统内存.写回到内存的操作会使得其他处理器中缓存了该地址的内存地址的数据失效.

在java程序执行的时候,会先将硬盘或者网络中的字节码数据,读取到内存中.内存的IO速度又小于CPU的缓存IO速度. 一般情况为了最大程序的提升效率, java程序会把变量资源放到CPU缓存中.
没有被volatile修饰的变量,只会存在CPU缓存中,不会被写回内存.在多核CPU的情况下,无法对多个线程所见.被volatile修饰的变量,被修改之后,会把修改的值写回内存中.
同时,在其他CPU缓存中如果缓存了该内存地址,也就是不是最新的值. 所以写回去之后要保证数据一致性.让其他CPU数据失效.
但是,问题在于如果我们大量使用volatile.会浪费CPU缓存资源.性能会降低.同时会禁用指令重排序, 抑制CPU本身的性能优化.

- 注意：volatile的语义不足以确保递增操作的原子性，这也就是为什么我们使用了volatile，还需要使用synchronized关键字的原因

