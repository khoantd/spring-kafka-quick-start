package dev.danvega.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	// Spring Boot picks up any NewTopic beans and hands them to a KafkaAdmin, which on startup asks the broker to create them.
	// The admin client uses an idempotent "create if not exists" call under the hood, so if greetings already exists,
	// the broker just shrugs and moves on.
	@Bean
    NewTopic greetings() {
		return TopicBuilder.name("greetings")
				.partitions(1)
				.replicas(1)
				.build();
	}

	@Bean
    ApplicationRunner runner(KafkaTemplate<String, String> template) {
		return args -> template.send("greetings", "Hello, Spring for Apache Kafka!");
	}

	@KafkaListener(topics = "greetings", groupId = "demo")
	public void listen(String message) {
		System.out.println("got: " + message);
	}
}
