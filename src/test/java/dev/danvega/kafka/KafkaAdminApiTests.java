package dev.danvega.kafka;

import dev.danvega.kafka.dto.CreateTopicRequest;
import dev.danvega.kafka.dto.MessageDto;
import dev.danvega.kafka.dto.SendMessageRequest;
import dev.danvega.kafka.dto.TopicDetail;
import dev.danvega.kafka.dto.TopicSummary;
import dev.danvega.kafka.service.MessageService;
import dev.danvega.kafka.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@EmbeddedKafka(topics = "greetings", partitions = 1)
public class KafkaAdminApiTests {

    @Autowired
    private TopicService topicService;

    @Autowired
    private MessageService messageService;

    @Test
    void listsTopics() {
        List<TopicSummary> topics = topicService.listTopics();
        assertThat(topics).extracting(TopicSummary::name).contains("greetings");
    }

    @Test
    void createsDescribesAndDeletesTopic() {
        topicService.createTopic(new CreateTopicRequest("api-topic", 2, (short) 1));
        try {
            TopicDetail detail = topicService.getTopic("api-topic");
            assertThat(detail.name()).isEqualTo("api-topic");
            assertThat(detail.partitions()).isEqualTo(2);
            assertThat(detail.partitionDetails()).hasSize(2);
        } finally {
            topicService.deleteTopic("api-topic");
        }
        assertThatThrownBy(() -> topicService.getTopic("api-topic"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void rejectsDuplicateTopic() {
        topicService.createTopic(new CreateTopicRequest("dupe-topic", 1, (short) 1));
        try {
            assertThatThrownBy(() -> topicService.createTopic(new CreateTopicRequest("dupe-topic", 1, (short) 1)))
                    .isInstanceOf(ResponseStatusException.class);
        } finally {
            topicService.deleteTopic("dupe-topic");
        }
    }

    @Test
    void sendsAndBrowsesMessages() {
        messageService.send("greetings", new SendMessageRequest("k1", "hello from api"));
        List<MessageDto> messages = messageService.browse("greetings", 10);
        assertThat(messages).extracting(MessageDto::value).contains("hello from api");
    }
}
