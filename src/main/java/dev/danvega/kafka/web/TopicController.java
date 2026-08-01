package dev.danvega.kafka.web;

import dev.danvega.kafka.dto.CreateTopicRequest;
import dev.danvega.kafka.dto.IncreasePartitionsRequest;
import dev.danvega.kafka.dto.TopicDetail;
import dev.danvega.kafka.dto.TopicSummary;
import dev.danvega.kafka.service.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public List<TopicSummary> list() {
        return topicService.listTopics();
    }

    @GetMapping("/{name}")
    public TopicDetail get(@PathVariable String name) {
        return topicService.getTopic(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicDetail create(@RequestBody CreateTopicRequest request) {
        return topicService.createTopic(request);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String name) {
        topicService.deleteTopic(name);
    }

    @PatchMapping("/{name}/partitions")
    public TopicDetail increasePartitions(@PathVariable String name, @RequestBody IncreasePartitionsRequest request) {
        return topicService.increasePartitions(name, request.partitions());
    }
}
