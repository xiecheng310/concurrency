### 1. 内置锁Synchronized
#### 1.1 使用方式
多线程的锁，其实本质上就是给一块内存空间的访问添加访问权限，因为Java中是没有办法直接对某一块内存进行操作的，又因为Java是面向对象的语言，一切皆对象，所以具体的表现就是某一个对象承担锁的功能，每一个对象都可以是一个锁。内置锁，使用方式就是使用 synchronized 关键字，synchronized 方法或者 synchronized 代码块。

```java
// 当前对象作为锁, 修饰普通方法 
private synchronized void function() {
    //TODO execute something
}

// 当前类的class对象作为锁，修饰静态方法
private static synchronized void function() {
    //TODO execute something
}

// 任意对象作为内置锁,修饰同步代码块
private void function() {
    synchronized (object) {
        //TODO execute something
    }
}

```
#### 1.2 互斥锁
所谓互斥锁, 指的是一次最多只能有一个线程持有的锁. 在jdk1.5之前, 我们通常使用synchronized机制控制多个线程对共享资源的访问. 而现在, Lock提供了比synchronized机制更广泛的锁定操作. 后面详解.

#### 1.3 从jvm层面理解synchronized
##### 1.3.1 字节码指令
通过javap工具分析class文件.观察带有synchronize关键字的部分.
```
0: 1dc
2: dup
3: astroe_1
4: monitorenter
... ...
17: monitorexit
18: ireturn
... ...
```
从字节码指令中可以看出,同步代码块的始终对应了monitorenter和monitorexit两个指令

#### 1.3.2 为什么任意对象都能作为内置锁(synchronized)
在jvm创建对象的时候, 对象头mark word中会有以下信息:
```
a. hash值 (object的hashCode方法可以获取hash码,但是是native方法)
b. GC分代年龄(为gc分代算法服务)
c. 锁状态标志
d. 线程持有的锁
e. 偏向时间id
f. 偏向时间戳
```

### 2. 其他锁的含义
#### 2.1 偏向锁
```
Java偏向锁(Biased Locking)是Java6引入的一项多线程优化。 偏向锁，顾名思义，它会偏向于第一个访问锁的线程.
a. 如果在运行过程中，同步锁只有一个线程访问，不存在多线程争用的情况，则线程是不需要触发同步的，这种情况下，就会给线程加一个偏向锁。 
b. 如果在运行过程中，遇到了其他线程抢占锁，则持有偏向锁的线程会被挂起，JVM会消除它身上的偏向锁，将锁恢复到标准的轻量级锁。
它通过消除资源无竞争情况下的同步原语，进一步提高了程序的运行性能。
```

#### 2.2 轻量级锁
```
轻量级锁是由偏向锁升级来的，偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向锁就会升级为轻量级锁.
```
#### 2.3 重量级锁
```	
普遍意义上的synchronized. 互斥锁.
```

#### 2.4 重入锁和非重入锁

1. 非重入锁: 当一个线程进入同步方法(代码块)时, 拿到锁, 其他线程只能等待锁竞争.
2. 重入锁 : 两个同步方法使用同一个对象锁, 当一个线程拿到锁之后, 进入同步方法, 在该方法中又调用了另一个同步方法, 能直接进入. 这种情况,就称为锁重入. 可以避免死锁.

```java
package com.cgg.concurrency.lesson05;

public class Demo01_ReentrantLock {

    public static void main(String[] args) {
        Demo01_ReentrantLock r = new Demo01_ReentrantLock();
        r.fun1();
    }

    public synchronized void fun1() {
        System.out.println("I'm fun1()");
        fun2(); // 锁重入， 两个同步方法使用同一把锁
    }

    private synchronized void fun2() {
        System.out.println("I'm fun2()");
    }
}
```
- 结果输出:
```
I'm fun1()
I'm fun2()
```
- 分析: 因为fun1()和fun2()都是使用当前对象作为锁对象, 所以当线程进入fun1()后,已经获取了锁.如果synchronize锁不能重入,那么fun2()不会执行,会出现死锁

#### 2.5 自旋锁
当一个线程进入同步,开始执行的时候, 另一个线程处于自旋等待.等待重新去竞争锁.
- 模拟自旋等待场景
```java
package com.cgg.concurrency.lesson05;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Demo02_SpinLock {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            begin();
        }

        while (Thread.activeCount() != 2) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println("我处于自旋等待状态...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("所有线程执行完成....");
    }

    private static void begin() {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "开始执行...");
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "执行完成...");
        }).start();
    }
}
```

#### 2.6 死锁

两个线程相互等待对方释放锁的现象，就是死锁

- 代码示例
```java
package com.cgg.concurrency.lesson05;

/**
 * @author 谢成
 */
public class Demo03_DeadLock {
	
	private Object obj1 = new Object();
	private Object obj2 = new Object();
	
	public void fun1() {
		synchronized (obj1) {
			try {
				System.out.println(Thread.currentThread().getName() + "拿到了锁obj1");
				Thread.sleep(100);
				System.out.println("等待另一个线程释放obj2...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (obj2) {
				System.out.println("fun1 ...");
			}
		}
	}
	
	public void fun2() {
		synchronized (obj2) {
			try {
				System.out.println(Thread.currentThread().getName() + "拿到了锁obj2");
				Thread.sleep(100);
				System.out.println("等待另一个线程释放obj1...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (obj1) {
				System.out.println("fun2 ...");
			}
		}
	}
	
	public static void main(String[] args) {
		Demo03_DeadLock d = new Demo03_DeadLock();
		new Thread(() -> d.fun1()).start();
		new Thread(() -> d.fun2()).start();
	}
	
}
```

- 输出结果：
```
Thread-0拿到了锁obj1
Thread-1拿到了锁obj2
等待另一个线程释放obj2...
等待另一个线程释放obj1...
```

- 避免死锁的方法：
1. 避免一个线程同时获取多个锁
2. 避免一个线程在锁内同时占有多个资源，尽量保证每个锁只占用一个资源
3. 尝试使用定时锁，使用lock.tryLock(timeout)来替代使用内置锁机制
4. 对于数据库，加锁和释放锁必须在一个数据库连接里，否则会出现解锁失效的情况
