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
import org.springframework.messaging.MessageChannel;
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

        Thread.sleep(10000);
        log.info("Sending sample messages...");
        send("1", "data1");
        send("2", "data2");
        send("1", "data1-again"); // Same payloadId as the first message
        send("3", "data3 ignore this");
        send("2", "data2-again"); // Same payloadId as the second message
        send("4", "data4");
        send("6", "data6");
        send("5", "data5");

        // Wait for some time to allow processing to complete
        Thread.sleep(10000);
        log.info("Finished sending sample messages.");
    }

    private void send(String id, String data) {
        MyPayload payload = new MyPayload(id, data);
        log.info("Sending message with payload: {}", payload);
        MessageChannel inputChannel = context.getBean("inputChannel", MessageChannel.class);
        inputChannel.send(MessageBuilder.withPayload(payload).build());
    }

}
