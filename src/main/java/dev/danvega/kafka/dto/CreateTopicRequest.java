package dev.danvega.kafka.dto;

public record CreateTopicRequest(String name, Integer partitions, Short replicationFactor) {
}
