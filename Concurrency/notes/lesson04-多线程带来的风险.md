#### 1.线程带来的风险
1. 活跃性问题
2. 性能问题线
3. 程安全问题


#### 2. 活跃性问题
1. 死锁
2. 线程饥饿（线程优先级）
- 原因
1. 高优先级线程吞噬低优先级线程的时间片

```
package com.cgg.concurrence.thread_02_safe;

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

```
最后的输出：

```
a ----79393
a ----79394
b ----122254
b ----122255

b > a，说明t2线程执行得多一些
```

2. 线程被永久堵塞在一个等待进入同步块的状态
3. 等待的线程永远不被唤醒
- 如何尽量避免？
1. 设置合理的优先级
2. 使用锁（LOCK）来代替synchronized
3. 活锁（相互礼让）

#### 3. 性能问题
多线程程序不一定就快。例如单核CPU。上下文切换非常浪费资源。而且如果考虑线程安全，那么对一些共享可变变量，会执行同步操作，这样会抑制cpu的指令重排序，且cpu缓存的使用非常少

#### 4. 线程安全问题
1. 代码示例

```
package com.cgg.concurrence.thread_02_safe;

import java.util.concurrent.TimeUnit;

public class Demo02_safe {

    private int value;

    private int getNext(){
        return value ++ ;
    }

    public static void main(String[] args) {
        Demo02_safe safe = new Demo02_safe();
        getNextValue(safe);
        getNextValue(safe);
        getNextValue(safe);
    }

    private static void getNextValue(Demo02_safe safe) {
        new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "..." + safe.getNext());
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

```
- 出现了问题

```
Thread-2...1
Thread-2...3
Thread-1...3
Thread-0...3
Thread-1...4
Thread-2...4
Thread-0...5
Thread-2...6
```
- 原因: 分析字节码文件

通过javap工具分析class文件
```
javap -verbose .\Demo02_safe.class
```
- 得到以下信息:

```
 public int getNext();
    descriptor: ()I
    flags: ACC_PUBLIC
    Code:
      stack=4, locals=1, args_size=1
         0: aload_0
         1: dup
         2: getfield      #2                  // Field value:I
         5: dup_x1
         6: iconst_1
         7: iadd
         8: putfield      #2                  // Field value:I
        11: ireturn
      LineNumberTable:
        line 10: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      12     0  this   Lcom/cgg/concurrence/thread_02_safe/Demo02_safe;
```
- 重点关注getNext()方法. 我们会发现return value++并非是一个原子性操作,它分为了以下几个步骤
    - getfield: 获取值
    - iadd: 在操作数栈中执行+1
    - putfield: 设置值

如果一个线程在设置值之前,被另一个前程抢占了cpu时间片,那另一个线程获取值就还是之前的值.因此会出现相同的值.

2. 如何解决?
- 最简单的办法,使用synchronized关键字修饰getNext()方法.
- 更多解决办法后面详解

3. 什么时候会有线程安全问题?

有且仅有以下几种条件,才会出现线程安全问题.
- 多线程环境
- 多个线程共享一个资源
- 对资源进行非原子性(不是一个字节码指令)的操作.

