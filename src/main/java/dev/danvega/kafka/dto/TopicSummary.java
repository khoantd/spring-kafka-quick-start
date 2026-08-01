package dev.danvega.kafka.dto;

public record TopicSummary(String name, int partitions, short replicationFactor, long totalMessages) {
}
