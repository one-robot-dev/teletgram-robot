package com.example.demo.utils.iologic;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IOLogicExecutors {

    public static final Executor executor;

    static {
        AtomicInteger threadCount = new AtomicInteger(0);
        int cpuNum = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(cpuNum, cpuNum * 2, 5, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("IOLogicThread-" + threadCount.getAndIncrement());
            return thread;
        });
    }
}
