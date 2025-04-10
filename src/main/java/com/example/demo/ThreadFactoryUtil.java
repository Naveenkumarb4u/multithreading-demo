package com.example.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadFactoryUtil {
    /*public static ThreadFactory evenThreadFactory() {
        AtomicInteger count = new AtomicInteger(1);
        return r -> new Thread(r, "even-thread-" + count.getAndIncrement());
    }

    public static ThreadFactory oddThreadFactory() {
        AtomicInteger count = new AtomicInteger(1);
        return r -> new Thread(r, "odd-thread-" + count.getAndIncrement());
    }*/
    // This map will store the payloadId to thread mappings
    /*private static final ConcurrentMap<Integer, Thread> threadMap = new ConcurrentHashMap<>();

    public static ThreadFactory threadFactory() {
        AtomicInteger count = new AtomicInteger(1);
        return r -> {
            Thread thread = new Thread(r, "thread-" + count.getAndIncrement());
            return thread;
        };
    }

    // Get a thread for the given payloadId. If it's not found, create a new one.
    public static Thread getThreadForPayloadId(int payloadId) {
        synchronized (threadMap) {
            if (!threadMap.containsKey(payloadId)) {
                Thread thread = new Thread("thread-" + payloadId);
                threadMap.put(payloadId, thread);
            }
            return threadMap.get(payloadId);
        }
    }*/
    // This map will store the payloadId to thread mappings
    private static final ConcurrentMap<Integer, Thread> threadMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, Lock> payloadIdLocks = new ConcurrentHashMap<>();
    private static int threadPoolSize;

    public static void setThreadPoolSize(int size) {
        threadPoolSize = size;
    }

    public static ExecutorService createThreadPool() {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

    public static ThreadFactory threadFactory() {
        AtomicInteger count = new AtomicInteger(1);

        return r -> {
            Thread thread = new Thread(r, "thread-" + count.getAndIncrement());
            return thread;
        };
    }

    // Get a thread for the given payloadId. If it's not found, create a new one.
    public static Thread getThreadForPayloadId(int payloadId) {
        System.out.println("Inside getThreadForPayloadId: "+ payloadId);
        synchronized (threadMap) {
            if (!threadMap.containsKey(payloadId)) {
                Thread thread = new Thread("thread-" + payloadId);
                threadMap.put(payloadId, thread);
            }
            return threadMap.get(payloadId);
        }
    }

    // Get a Lock for the tradeId, ensuring the same tradeId gets the same lock
    public static Lock getLockForPayloadId(int tradeId) {
        synchronized (payloadIdLocks) {
            if (!payloadIdLocks.containsKey(tradeId)) {
                payloadIdLocks.put(tradeId, new ReentrantLock());
            }
            return payloadIdLocks.get(tradeId);
        }
    }
}