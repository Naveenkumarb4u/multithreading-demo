package com.multithreading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TaskSchedulerConfig {

    @Bean(name = "payloadTaskExecutor")
    public ThreadPoolExecutor payloadTaskExecutor() {
        int poolSize = 5;
        int queueCapacity = 100;
        long keepAliveTime = 60;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(queueCapacity);
        ThreadFactory threadFactory = new PayloadIdThreadFactory("thread-");
        // Remove RejectedExecutionHandler for blocking behavior
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize, poolSize, keepAliveTime, unit,
                workQueue, threadFactory);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    static class PayloadIdThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        PayloadIdThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon()) t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}