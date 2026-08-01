package dev.danvega.kafka.dto;

import java.util.List;

public record PartitionDetail(int partition, int leader, List<Integer> replicas, List<Integer> isr,
                              long beginningOffset, long endOffset) {
}
