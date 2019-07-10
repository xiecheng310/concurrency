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