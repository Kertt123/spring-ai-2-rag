package com.serkowski.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatCompletionService {

    private final ChatClient chatClient;

    public ChatCompletionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getCompletions(String message, String conversationId) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}

