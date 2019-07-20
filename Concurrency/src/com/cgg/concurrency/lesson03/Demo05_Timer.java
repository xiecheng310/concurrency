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