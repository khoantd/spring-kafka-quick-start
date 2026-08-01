package dev.danvega.kafka.service;

import dev.danvega.kafka.dto.MessageDto;
import dev.danvega.kafka.dto.SendMessageRequest;
import dev.danvega.kafka.dto.SendMessageResponse;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class MessageService {

    private static final int MAX_BROWSE_LIMIT = 500;
    private static final int BROWSE_TIMEOUT_MILLIS = 4000;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;

    public MessageService(KafkaTemplate<String, String> kafkaTemplate, ConsumerFactory<String, String> consumerFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
    }

    public SendMessageResponse send(String topic, SendMessageRequest request) {
        String value = request.value();
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message value is required");
        }
        try {
            SendResult<String, String> result = request.key() == null || request.key().isBlank()
                    ? kafkaTemplate.send(topic, value).get(10, TimeUnit.SECONDS)
                    : kafkaTemplate.send(topic, request.key(), value).get(10, TimeUnit.SECONDS);
            RecordMetadata metadata = result.getRecordMetadata();
            return new SendMessageResponse(topic, metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted while sending message", e);
        } catch (ExecutionException e) {
            String reason = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to send message: " + reason, e);
        } catch (TimeoutException e) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Timed out sending message", e);
        }
    }

    public List<MessageDto> browse(String topic, int limit) {
        int boundedLimit = Math.max(1, Math.min(limit <= 0 ? 50 : limit, MAX_BROWSE_LIMIT));
        String groupId = "kafka-manager-browse-" + UUID.randomUUID();
        try (Consumer<String, String> consumer = consumerFactory.createConsumer(groupId)) {
            List<PartitionInfo> partitions = consumer.partitionsFor(topic, Duration.ofSeconds(5));
            if (partitions == null || partitions.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found: " + topic);
            }
            List<TopicPartition> topicPartitions = partitions.stream()
                    .map(partition -> new TopicPartition(topic, partition.partition()))
                    .toList();
            consumer.assign(topicPartitions);

            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
            int perPartition = (int) Math.ceil((double) boundedLimit / topicPartitions.size());
            for (Map.Entry<TopicPartition, Long> entry : endOffsets.entrySet()) {
                long end = entry.getValue();
                long beginning = consumer.beginningOffsets(List.of(entry.getKey())).get(entry.getKey());
                consumer.seek(entry.getKey(), Math.max(beginning, end - perPartition));
            }

            List<MessageDto> messages = new ArrayList<>();
            long deadline = System.currentTimeMillis() + BROWSE_TIMEOUT_MILLIS;
            while (messages.size() < boundedLimit && System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(400));
                if (records.isEmpty()) {
                    break;
                }
                for (ConsumerRecord<String, String> record : records) {
                    messages.add(new MessageDto(topic, record.partition(), record.offset(), record.timestamp(),
                            record.key(), record.value()));
                }
            }
            messages.sort(Comparator.comparingInt(MessageDto::partition).thenComparingLong(MessageDto::offset));
            return messages.stream().limit(boundedLimit).toList();
        }
    }
}
