package dev.danvega.kafka.dto;

import java.util.List;

public record TopicDetail(String name, int partitions, short replicationFactor, List<PartitionDetail> partitionDetails) {
}
