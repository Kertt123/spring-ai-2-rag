package com.serkowski.controller;

import com.serkowski.model.text.TextRequest;
import com.serkowski.services.ChatCompletionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatCompletionService chatService;

    public ChatController(ChatCompletionService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/text")
    String text(@RequestBody TextRequest requestBody) {
        return chatService.getCompletions(requestBody.message(), requestBody.conversationId());
    }
}
