package dev.danvega.kafka.dto;

public record SendMessageResponse(String topic, int partition, long offset) {
}
