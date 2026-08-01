package dev.danvega.kafka.dto;

public record MessageDto(String topic, int partition, long offset, long timestamp, String key, String value) {
}
