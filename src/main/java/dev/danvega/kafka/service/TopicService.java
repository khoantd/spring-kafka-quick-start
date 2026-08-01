package dev.danvega.kafka.service;

import dev.danvega.kafka.dto.CreateTopicRequest;
import dev.danvega.kafka.dto.PartitionDetail;
import dev.danvega.kafka.dto.TopicDetail;
import dev.danvega.kafka.dto.TopicSummary;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final AdminClient adminClient;

    public TopicService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public List<TopicSummary> listTopics() {
        try {
            Set<String> names = adminClient.listTopics().names().get();
            List<String> sortedNames = names.stream().sorted().toList();
            if (sortedNames.isEmpty()) {
                return List.of();
            }
            Map<String, TopicDescription> descriptions = adminClient.describeTopics(sortedNames).allTopicNames().get();
            Map<TopicPartition, Long> endOffsets = fetchOffsets(descriptions, OffsetSpec.latest());
            Map<TopicPartition, Long> beginningOffsets = fetchOffsets(descriptions, OffsetSpec.earliest());

            return sortedNames.stream().map(name -> {
                TopicDescription description = descriptions.get(name);
                long totalMessages = description.partitions().stream()
                        .mapToLong(partition -> endOffsets.getOrDefault(new TopicPartition(name, partition.partition()), 0L)
                                - beginningOffsets.getOrDefault(new TopicPartition(name, partition.partition()), 0L))
                        .sum();
                short replicationFactor = description.partitions().stream()
                        .findFirst()
                        .map(partition -> (short) partition.replicas().size())
                        .orElse((short) 0);
                return new TopicSummary(name, description.partitions().size(), replicationFactor, totalMessages);
            }).toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted while listing topics", e);
        } catch (ExecutionException e) {
            throw kafkaError("Failed to list topics", e);
        }
    }

    public TopicDetail getTopic(String name) {
        try {
            Map<String, TopicDescription> descriptions = adminClient.describeTopics(List.of(name)).allTopicNames().get();
            TopicDescription description = descriptions.get(name);
            if (description == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found: " + name);
            }
            Map<TopicPartition, Long> endOffsets = fetchOffsets(Map.of(name, description), OffsetSpec.latest());
            Map<TopicPartition, Long> beginningOffsets = fetchOffsets(Map.of(name, description), OffsetSpec.earliest());

            List<PartitionDetail> partitionDetails = description.partitions().stream()
                    .map(partition -> new PartitionDetail(
                            partition.partition(),
                            partition.leader() == null ? -1 : partition.leader().id(),
                            partition.replicas().stream().map(Node::id).toList(),
                            partition.isr().stream().map(Node::id).toList(),
                            beginningOffsets.getOrDefault(new TopicPartition(name, partition.partition()), 0L),
                            endOffsets.getOrDefault(new TopicPartition(name, partition.partition()), 0L)))
                    .sorted(Comparator.comparingInt(PartitionDetail::partition))
                    .toList();

            short replicationFactor = description.partitions().stream()
                    .findFirst()
                    .map(partition -> (short) partition.replicas().size())
                    .orElse((short) 0);
            return new TopicDetail(name, description.partitions().size(), replicationFactor, partitionDetails);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted while describing topic " + name, e);
        } catch (ExecutionException e) {
            throw kafkaError("Failed to describe topic " + name, e);
        }
    }

    public TopicDetail createTopic(CreateTopicRequest request) {
        String name = request.name();
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic name is required");
        }
        int partitions = request.partitions() == null ? 1 : request.partitions();
        short replicationFactor = request.replicationFactor() == null ? 1 : request.replicationFactor();
        if (partitions < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Partitions must be at least 1");
        }
        if (replicationFactor < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Replication factor must be at least 1");
        }
        try {
            adminClient.createTopics(List.of(new NewTopic(name, partitions, replicationFactor))).all().get();
            return describeWithRetry(name, 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted while creating topic " + name, e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Topic already exists: " + name);
            }
            throw kafkaError("Failed to create topic " + name, e);
        }
    }

    public void deleteTopic(String name) {
        try {
            adminClient.deleteTopics(List.of(name)).all().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted while deleting topic " + name, e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found: " + name);
            }
            throw kafkaError("Failed to delete topic " + name, e);
        }
    }

    public TopicDetail increasePartitions(String name, int newPartitionCount) {
        int current = getTopic(name).partitions();
        if (newPartitionCount <= current) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "New partition count must be greater than current count " + current);
        }
        try {
            adminClient.createPartitions(Map.of(name, NewPartitions.increaseTo(newPartitionCount))).all().get();
            return getTopic(name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Interrupted while increasing partitions for " + name, e);
        } catch (ExecutionException e) {
            throw kafkaError("Failed to increase partitions for " + name, e);
        }
    }

    private TopicDetail describeWithRetry(String name, int attempts) throws InterruptedException {
        for (int i = 0; i < attempts; i++) {
            try {
                return getTopic(name);
            } catch (ResponseStatusException e) {
                if (e.getStatusCode() != HttpStatus.BAD_GATEWAY || i == attempts - 1) {
                    throw e;
                }
            }
            Thread.sleep(200);
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to describe topic " + name + " after creation");
    }

    private Map<TopicPartition, Long> fetchOffsets(Map<String, TopicDescription> descriptions, OffsetSpec spec)
            throws ExecutionException, InterruptedException {
        List<TopicPartition> partitions = new ArrayList<>();
        descriptions.forEach((topicName, description) ->
                description.partitions().forEach(partition ->
                        partitions.add(new TopicPartition(topicName, partition.partition()))));
        if (partitions.isEmpty()) {
            return Map.of();
        }
        Map<TopicPartition, OffsetSpec> specs = partitions.stream()
                .collect(Collectors.toMap(topicPartition -> topicPartition, topicPartition -> spec));
        return adminClient.listOffsets(specs).all().get().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().offset()));
    }

    private ResponseStatusException kafkaError(String message, ExecutionException e) {
        String reason = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, message + ": " + reason, e);
    }
}
