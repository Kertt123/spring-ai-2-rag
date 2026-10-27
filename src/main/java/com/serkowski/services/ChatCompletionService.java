package com.serkowski.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.stereotype.Service;

@Service
public class ChatCompletionService {

    private final ChatClient chatClient;

    public ChatCompletionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getCompletions(String message, String conversationId, String type) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(a -> a.param(
                        VectorStoreDocumentRetriever.FILTER_EXPRESSION,
                        "type == '" + type + "'"
                ))
                .call()
                .content();
    }
}

