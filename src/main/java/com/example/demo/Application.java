package com.example.demo;

import com.example.demo.model.MyPayload;
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
        for (int i = 1; i <= 3; i++) {
            Message<MyPayload> message = MessageBuilder.withPayload(new MyPayload(i)).build();
            context.getBean("inputChannel", org.springframework.messaging.MessageChannel.class).send(message);
        }
        Thread.sleep(3000);
        log.info("Sending duplicate message...");
        Message<MyPayload> message1 = MessageBuilder.withPayload(new MyPayload(1)).build();
        context.getBean("inputChannel", org.springframework.messaging.MessageChannel.class).send(message1);
        Thread.sleep(3000);
        log.info("Sending one more duplicate message...");
        Message<MyPayload> message2 = MessageBuilder.withPayload(new MyPayload(1)).build();
        context.getBean("inputChannel", org.springframework.messaging.MessageChannel.class).send(message1);
        Thread.sleep(2000); // Give time for async processing
        log.info("Sending more and more duplicate messages...");
        for (int i = 1; i <= 3; i++) {
            Message<MyPayload> message = MessageBuilder.withPayload(new MyPayload(i)).build();
            context.getBean("inputChannel", org.springframework.messaging.MessageChannel.class).send(message);
        }
        Thread.sleep(3000);
    }
}
