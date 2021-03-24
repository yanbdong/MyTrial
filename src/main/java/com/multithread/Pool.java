package com.multithread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

/**
 * @author yanbdong@cienet.com.cn
 * @since Mar 17, 2021
 */

class Pool {

    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(() -> System.out.println("1"));
    }

}
