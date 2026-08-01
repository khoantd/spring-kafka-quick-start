package dev.danvega.kafka.web;

import dev.danvega.kafka.dto.MessageDto;
import dev.danvega.kafka.dto.SendMessageRequest;
import dev.danvega.kafka.dto.SendMessageResponse;
import dev.danvega.kafka.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topics/{topic}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageDto> browse(@PathVariable String topic, @RequestParam(defaultValue = "50") int limit) {
        return messageService.browse(topic, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SendMessageResponse send(@PathVariable String topic, @RequestBody SendMessageRequest request) {
        return messageService.send(topic, request);
    }
}
