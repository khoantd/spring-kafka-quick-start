package dev.danvega.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(topics = "greetings", partitions = 1)
public class KafkaMessageTests {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void sendsAndReceivesMessage() {
        var consumerProps = KafkaTestUtils.consumerProps(embeddedKafka, "test-group", true);
        try (Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProps, new StringDeserializer(), new StringDeserializer())) {

            consumer.subscribe(java.util.List.of("greetings"));

            // Drain anything the app already produced on startup
            KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(1));
            kafkaTemplate.send("greetings", "hello from the test");
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "greetings", Duration.ofSeconds(5));

            assertThat(record.value()).isEqualTo("hello from the test");
        }
    }

}
