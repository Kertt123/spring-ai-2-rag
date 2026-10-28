package com.serkowski.services;

import com.serkowski.model.DeviceType;
import com.serkowski.model.Tag;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatCompletionService {

    private final ChatClient chatClient;

    public ChatCompletionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getCompletions(String message, String conversationId, DeviceType type, List<Tag> tags) {
        String finalFilter = buildFilter(type, tags);
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(a -> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, finalFilter))
                .call()
                .content();
    }

    private static @NonNull String buildFilter(DeviceType type, List<Tag> tags) {
        String filter = "type == '" + type.value() + "'";
        if (tags != null && !tags.isEmpty()) {
            filter += " && tags in [" + tags.stream().map(t -> "'" + t.value() + "'").collect(Collectors.joining(",")) + "]";
        }
        return filter;
    }
}
