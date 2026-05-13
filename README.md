# Getting Started with Spring for Apache Kafka

A minimal demo for developers new to Kafka who want to send and receive their first message in Spring Boot.

## What you'll learn

- How Spring for Apache Kafka fits into a Spring Boot app
- How to produce messages with `KafkaTemplate`
- How to consume messages with `@KafkaListener`
- How to create a topic from code with `NewTopic`
- How to run a Kafka broker locally with zero ceremony using Spring Boot's Docker Compose support

## Why Kafka

Most Spring services start synchronously. Service A calls Service B over REST, B calls C, and everyone waits for everyone. That works until it doesn't: one slow service drags down the whole chain, adding a new consumer means redeploying the producer, and a missed call is gone forever.

Kafka flips the model. Services publish events that describe what just happened. Anyone interested subscribes. The producer doesn't know who's listening, and new listeners can show up later without asking. Producers stay small, consumers come and go, and the system keeps moving.

## Is Kafka the right tool?

Worth being honest. Kafka shines in some places and is overkill in others.

**Reach for it when:**

- You have high-volume event streams (clicks, telemetry, transactions)
- Multiple teams want the same data without coordinating
- You need to replay history to rebuild a cache or backfill a feature
- Ordering within a key matters (all events for one user, in order)
- The log is your source of truth (event sourcing, CQRS)

**Skip it when:**

- You just need a job queue with a couple of workers (RabbitMQ or SQS is simpler)
- You want request/response (Kafka is one-way)
- You have low traffic and no appetite for running infrastructure
- You need per-message TTL or priority lanes

For the demo, we're using Kafka because it's what we're here to learn. In a real app, match the tool to the problem.

## Core vocabulary

Four words cover almost everything you'll do with Kafka. Internalize these and the rest follows.

- **Topic** — a named stream of events. Like a table name, but for facts: `orders`, `payments`, `page-views`.
- **Partition** — a topic split for parallelism. Events with the same key always go to the same partition, so order is preserved per key. We use one partition in this demo.
- **Offset** — a position in a partition. A monotonically increasing number that tracks how far a consumer has read. Kafka remembers it across restarts.
- **Consumer group** — a team sharing the work. Consumers with the same `groupId` split partitions between them. Add an instance and it gets work automatically.

A **broker** is the server process that holds the topics. For local dev we run a single broker; in production you run a cluster.

## Requirements

- Java 21+
- Docker (with Compose)
- Maven (or use the wrapper)

## Run it

```bash
./mvnw spring-boot:run
```

Spring Boot starts the Kafka container, waits for it to be ready, wires up `spring.kafka.bootstrap-servers`, and shuts everything down when you stop the app.

You should see this in the logs:

```
got: Hello, Spring for Apache Kafka!
```

## The code

The whole app is one file:

```java
@SpringBootApplication
public class KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }

    @Bean
    NewTopic greetings() {
        return TopicBuilder.name("greetings").partitions(1).replicas(1).build();
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
```

Three pieces worth understanding:

### The `NewTopic` bean

Spring Boot picks up any `NewTopic` beans on startup and asks the broker to create them if they don't exist. The call is idempotent, so it's safe to leave in. If you bump `.partitions(1)` to `.partitions(3)` later, Spring will increase the partition count on the existing topic too.

You don't strictly need this. Kafka can auto-create topics. But declaring topics in code keeps your setup explicit and version-controlled.

### `KafkaTemplate` for producing

`KafkaTemplate` is the producer abstraction. Spring Boot auto-configures one as a bean. Calling `template.send(topic, message)` is non-blocking. It returns a `CompletableFuture` you can chain off of if you care about the result.

For this demo we send one message at startup using `ApplicationRunner`. In a real app you'd inject the template into a service and call `send` from wherever a message needs to go.

### `@KafkaListener` for consuming

`@KafkaListener` turns a method into a consumer. Spring runs it on a background thread, polls the broker, and invokes the method for each message. The `groupId` ties this consumer to a consumer group, which is how Kafka tracks offsets and load-balances across multiple instances.

## Configuration

`application.properties` only needs one line:

```properties
spring.kafka.consumer.auto-offset-reset=earliest
```

This tells a fresh consumer group to start reading from the beginning of the topic. Without it, the consumer only sees messages sent after it joins, which can race with the `ApplicationRunner` that sends the message on startup.

A few other properties worth knowing:

| Property | What it does |
| --- | --- |
| `spring.kafka.bootstrap-servers` | Broker address. Auto-set by Docker Compose support. Override to point at a different broker. |
| `spring.kafka.consumer.group-id` | Default group ID if your `@KafkaListener` doesn't specify one. |
| `spring.kafka.producer.acks` | How many broker acknowledgements before `send` is considered successful. |
| `logging.level.org.apache.kafka` | Set to `WARN` to quiet the noisy client config dumps. |

## Running Kafka locally

This project uses Spring Boot's Docker Compose support to manage the broker. Add the dependency and a `compose.yaml`, and Spring takes care of the lifecycle.

### The dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-docker-compose</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### The simple compose file

```yaml
services:
  kafka:
    image: apache/kafka:3.8.0
    ports:
      - "9092:9092"
```

The `apache/kafka` image ships with KRaft mode defaults for a single-node local setup. No ZooKeeper, no cluster IDs, no listener maps. Spring Boot recognizes the image as Kafka and auto-configures the connection.

### Useful Docker Compose properties

| Property | What it does |
| --- | --- |
| `spring.docker.compose.lifecycle-management` | `start-and-stop` (default), `start-only`, or `none`. Use `start-only` to keep containers running between restarts. |
| `spring.docker.compose.file` | Point at a different compose file (e.g. `compose-with-ui.yaml`). |
| `spring.docker.compose.enabled` | Set to `false` to skip Compose entirely (e.g. when Kafka is already running elsewhere). |

## Optional: add a Kafka UI

When you want to see what's actually happening inside the broker (topics, messages, consumer groups), swap in the fuller compose file. This adds [Kafka UI](https://github.com/provectus/kafka-ui) on `localhost:8081`.

```yaml
services:
  kafka:
    image: apache/kafka:3.8.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://:29092,CONTROLLER://:9093,PLAINTEXT_HOST://:9092
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      CLUSTER_ID: "4L6g3nShT-eMCtK--X86sw"

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    ports:
      - "8081:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
    depends_on:
      - kafka
```

The extra config exists because two clients now need to reach the broker from two different network contexts. Your Spring app runs on the host and connects via `localhost:9092`. The UI runs inside Docker and connects via `kafka:29092`. Kafka has to advertise the right address to each one. Everything else is KRaft mode boilerplate the image wants spelled out once you override defaults.

## Where to go next

- Send and receive a POJO instead of a string (Spring will serialize with Jackson)
- Add a second `@KafkaListener` and watch the consumer group rebalance
- Increase partitions and run multiple instances to see load balancing
- Wire up a `KafkaTransactionManager` for exactly-once semantics
- Explore Spring for Apache Kafka's [reference docs](https://docs.spring.io/spring-kafka/reference/)

## Troubleshooting

**Producer or consumer logs are noisy.** Kafka clients dump every config value at startup. Quiet them down with `logging.level.org.apache.kafka=WARN`.

**Kafka UI shows the cluster as offline.** The broker is reachable from your host but not from inside Docker. Make sure you're using the full compose file with both listeners declared.

**`ReadinessTimeoutException` after 2 minutes.** The broker isn't accepting connections on port 9092. Run `docker compose up` directly (not through Spring) and watch the Kafka container's logs for a startup error.