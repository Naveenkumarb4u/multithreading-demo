package com.multithreading;

import com.multithreading.model.MyPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
@ImportResource("classpath:integration-context.xml")
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private final ApplicationContext context;

    public Application(ApplicationContext context) {
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(1000);
        log.info("Sending New message...");
        for (int i = 1; i <= 3; i++) {
            Message<MyPayload> message = MessageBuilder.withPayload(new MyPayload("" + i)).build();
            
            context.getBean("inputChannel", org.springframework.messaging.MessageChannel.class).send(message);
        }
        log.info("Sending same message again...");
        for (int i = 1; i <= 3; i++) {
            Message<MyPayload> message = MessageBuilder.withPayload(new MyPayload("" + i)).build();
            context.getBean("inputChannel", org.springframework.messaging.MessageChannel.class).send(message);
        }

    }
}
