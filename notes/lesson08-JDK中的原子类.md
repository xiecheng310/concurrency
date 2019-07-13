### 1. 原子类的分类和使用
1. 原子更新基本类型
2. 原子更新数组
3. 原子更新抽象类型
4. 原子更新字段

#### 1.1 原子更新基本类型
使用原子类解决lesson04中的线程安全问题
```java
package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo01_unsafe {

    private AtomicInteger value = new AtomicInteger(0);

    private int getNext(){
        return value.getAndIncrement() ; 
    }

    public static void main(String[] args) {
        Demo01_unsafe unsafe = new Demo01_unsafe();
        getNextValue(unsafe);
        getNextValue(unsafe);
        getNextValue(unsafe);
    }

    private static void getNextValue(Demo01_unsafe unsafe) {
        new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "..." + unsafe.getNext());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
```

#### 1.2 原子更新数组类型
```java
package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:13
 */
public class Demo02_atomicArray {
    private int[] arr = {1, 2, 3};
    private AtomicIntegerArray aia = new AtomicIntegerArray(arr);

    private int getArrNext() {
        int add = aia.getAndAdd(1, 10);//索引1的元素+10
        int increment = aia.getAndIncrement(1);// 索引1的元素自增1
        return add;
    }
}
```

#### 1.3原子更新抽象类型(引用)
准备一个User类
```java
/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:14
 */
public class User {
    private String name;
    volatile int age;  // 后面原子更新字段，基于反射设置值，不能声明为private

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
```
注意这里是对User对象引用的原子操作，而不是对User实例属性的原子操作
```java
/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:15
 */
public class Demo03_atomicReference {
    private AtomicReference<User> user;

    public User getUser() {
        return user.getAndSet(new User());
    }
}

```

#### 1.4 原子更新对象属性
```java
package com.cgg.concurrency.lesson08;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author Donavon Xie
 * CREATE AT 2019/7/14 0:20
 */
public class Demo04_atomicObjectProperty {
    private User init = new User("tom", 10);
    // age字段必须要volatile修饰，且不能为private
    private static final AtomicIntegerFieldUpdater<User> user =
            AtomicIntegerFieldUpdater.newUpdater(User.class, "age");

    public int getUserAge() {
        return user.getAndIncrement(init);
    }
    
    public static void main(String[] args) {
        Demo04_atomicObjectProperty demo = new Demo04_atomicObjectProperty();
        getNextAge(demo);
        getNextAge(demo);
        getNextAge(demo);

    }
    
    private static void getNextAge(Demo04_atomicObjectProperty demo) {
        new Thread(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "..." + demo.getUserAge());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

```
输出结果为：
```
Thread-0...10
Thread-1...11
Thread-2...12
Thread-1...13
Thread-2...15
Thread-0...14
Thread-0...16

```

### 原子类实现原理
底层都是使用unsafe类实现，使用到了CAS（底层也是unsafe）操作（compare and set）,如下：
```java
public final int updateAndGet(IntUnaryOperator updateFunction) {
    int prev, next;
    do {
        prev = get();
        next = updateFunction.applyAsInt(prev);
    } while (!compareAndSet(prev, next));
    return next;
}
```
1. 首先获取当前值prev
2. 再进行更新操作，获取值next
3. 如果CAS操作返回true，就返回更新后的值，如果为false，即已经被其他线程修改，就再次执行刚才的操作。