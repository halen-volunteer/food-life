package com.hmdp.ai.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "openAiStreamingChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = {"shopTool", "reservationTool", "voucherTool"}
)
public interface ConsultantService {

    @SystemMessage(fromResource = "system.txt")
    Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);
}
