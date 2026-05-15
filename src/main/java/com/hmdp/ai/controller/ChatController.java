package com.hmdp.ai.controller;

import com.hmdp.ai.aiservice.ConsultantService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Resource
    private ConsultantService consultantService;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestParam String memoryId, @RequestParam String message) {
        return consultantService.chat(memoryId, message)
                .map(content -> ServerSentEvent.<String>builder()
                        .event("message")
                        .data(content)
                        .build());
    }
}
