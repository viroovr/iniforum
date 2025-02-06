//package com.forum.project.infrastructure.persistence.kafka;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(classes = KafkaTestConfig.class)
//@EmbeddedKafka(partitions = 1, topics = {"test-topic"})
//@ActiveProfiles("test")
//public class KafkaTest {
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
//
//    private final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
//
//    @KafkaListener(topics = "test-topic", groupId = "test-group")
//    public void listen(ConsumerRecord<String, String> record) {
//        records.add(record);
//    }
//
//    @Test
//    public void testSendAndReceiveMessage() throws InterruptedException {
//        kafkaTemplate.send("test-topic", "key", "Hello, Embedded Kafka!");
//
//        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
//
//        assertThat(received).isNotNull();
//        assertThat(received.value()).isEqualTo("Hello, Embedded Kafka!");
//    }
//}
