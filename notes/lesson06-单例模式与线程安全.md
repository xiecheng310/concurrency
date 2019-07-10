### 1. 单例模式
在多线程环境下, 如果我们只想创建一个对象,比如各种连接池对象,工厂对象 那么我们会使用到单例模式.可以分为饿汉式和懒汉式
1. 饿汉式
```java
package com.cgg.concurrency.lesson06;
class SingletonHungry {
    private SingletonHungry() {
        // 私有构造方法,不能通过new创建对象
    }
    private SingletonHungry instance = new SingletonHungry(); // 每次加载类,实例化对象
    public SingletonHungry getInstance() {
        return instance;
    }
}
```
饿汉式不会有线程安全问题, 但是每次这个类被加载,都会实例化一个对象,可能这个对象不被使用,造成资源浪费

2. 懒汉式
```java
package com.cgg.concurrency.lesson06;
class SingletonLazy_V1 {
    private SingletonLazy_V1() {
        // 私有构造,不能new
    }
    private static SingletonLazy_V1 instance;
    public static SingletonLazy_V1 getInstance() {
        if (instance == null) {     // 这里出现了非原子性的操作
            instance = new SingletonLazy_V1();
        }
        return instance;
    }
}
```
先判断,再创建对象并赋值是一个非原子性的操作. 会出现线程安全问题, 验证代码如下:

```java
package com.cgg.concurrency.lesson06;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class CheckThreadSafe {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            service.execute(() ->
                System.out.println(Thread.currentThread().getName() + SingletonLazy_V1.getInstance())
            );
        }
        service.shutdown();
    }
}
```

结果输出:
```
pool-1-thread-1com.cgg.concurrency.lesson06.SingletonLazy_V1@5ea9368c
pool-1-thread-4com.cgg.concurrency.lesson06.SingletonLazy_V1@1c3f9914
pool-1-thread-5com.cgg.concurrency.lesson06.SingletonLazy_V1@1c3f9914
pool-1-thread-6com.cgg.concurrency.lesson06.SingletonLazy_V1@1c3f9914
```
从结果中可以看出, 并不是单例.

### 2. 如何解决单例模式中的线程安全问题
#### 2.1 在方法getInstance()上添加synchronize
```java
package com.cgg.concurrency.lesson06;
public class SingletonLazy_V2 {
    private SingletonLazy_V2() {
        // 私有构造,不能new
    }
    private static SingletonLazy_V2 instance;
    public static synchronized SingletonLazy_V2 getInstance() {
        if (instance == null) {
            instance = new SingletonLazy_V2();
        }
        return instance;
    }
}
```
这样虽然基本解决了线程安全问题,但是会有更大的隐患出现,虽然JDK6之后引入了偏向锁和轻量级锁, 但是在这个案例中并不适用.因为:
1. 不是单个线程在执行,所以偏向锁不适用, 会升级为轻量级锁
2. 升级为轻量级锁之后, 第一个线程进入同步代码块开始执行, 第二个线程也进入同步代码块,但是无法执行,因此轻量级锁开始自旋, 自旋是非常消耗性能的,类似于while (true)的操作. 所以最终偏向锁和轻量级锁都将升级为重量级锁, 变为单线程执行.


#### 2.2 使用同步代码块
使用双重检查加锁.缩小同步范围.并且只有在一次读取为null的时候才进行写操作.
```java
package com.cgg.concurrency.lesson06;

public class SingletonLazy_V3 {
    private SingletonLazy_V3() {
        // 私有构造,不能new
    }
    private static SingletonLazy_V3 instance;
    public static SingletonLazy_V3 getInstance() {
        if (instance == null) {
            synchronized (SingletonLazy_V3.class) {
                if (instance == null) {
                    instance = new SingletonLazy_V3();
                }
            }
        }
        return instance;
    }
}
```

但是还有一个小问题. 指令重排序问题. 如: 创建对象的时候,有以下一些指令:

```java
/*
 * 这行代码会产生以下3个指令
 * 1. 申请内存空间 
 * 2. 在空间中实例化一个对象
 * 3. 将instance的引用指向这块空间
 * 但是, 在cpu指令重排序后, 可能先执行了3, 导致instance == null 判断有误.
 */
instance = new SingletonLazy();
```

解决方案,就是在成员变量instance 上添加关键字volatile.禁止指令重排序。 所以最佳方案如下:

```java
package com.cgg.concurrency.lesson06;

public class SingletonLazy_V4 {
    private SingletonLazy_V4() {
        // 私有构造,不能new
    }
    private static volatile SingletonLazy_V4 instance;  // volatile禁止指令重排序
    public static SingletonLazy_V4 getInstance() {
        if (instance == null) {
            synchronized (SingletonLazy_V4.class) {
                if (instance == null) {
                    instance = new SingletonLazy_V4();
                }
            }
        }
        return instance;
    }
}
```
