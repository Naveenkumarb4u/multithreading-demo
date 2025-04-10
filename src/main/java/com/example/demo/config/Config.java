
package com.example.demo.config;

import com.example.demo.ThreadFactoryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${thread.pool.size}")
    private int threadPoolSize;

    @Bean
    public ThreadFactoryUtil threadFactoryUtil() {
        // Set the thread pool size dynamically
        ThreadFactoryUtil.setThreadPoolSize(threadPoolSize);
        return new ThreadFactoryUtil();
    }
}
