package com.serkowski.configuration;

import com.serkowski.services.ChatCompletionService;
import com.serkowski.services.VectorStoreService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, JdbcChatMemoryRepository chatMemoryRepository, VectorStore vectorStore) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder()
                                        .chatMemoryRepository(chatMemoryRepository)
                                        .maxMessages(30)
                                        .build())
                                .build(),
                        new SimpleLoggerAdvisor()
//                        RetrievalAugmentationAdvisor.builder()
//                                .documentRetriever(VectorStoreDocumentRetriever.builder()
//                                        .similarityThreshold(0.3)
//                                        .topK(4)
//                                        .vectorStore(vectorStore)
//                                        .build())
//                                .build()
                )
                .build();
    }


    @Bean
    public ChatCompletionService chatService(ChatClient chatClient) {
        return new ChatCompletionService(chatClient);
    }

    @Bean
    public VectorStoreService vectorStoreService(VectorStore vectorStore) {
        return new VectorStoreService(vectorStore);
    }
}
