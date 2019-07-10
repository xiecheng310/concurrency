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