# Spring AI 2 RAG with OpenAI

This project demonstrates Retrieval-Augmented Generation (RAG) with Spring AI 2, OpenAI models and PostgreSQL with
pgvector.

## Features

*   **ETL / Vector store:** Upload a PDF or text file. It is read (Tika for PDF, `TextReader` otherwise), split into
    chunks with `TokenTextSplitter`, then embedded and stored in pgvector.
*   **RAG chat:** `RetrievalAugmentationAdvisor` with query compression and query rewriting (both done by a small
    `gpt-4.1-nano` model), followed by similarity search (top 4, threshold 0.3).
*   **Chat memory:** Conversation history (last 30 messages) is kept in PostgreSQL via `JdbcChatMemoryRepository`,
    per `conversationId`.

## Getting Started

### Prerequisites

*   Java 25
*   Maven
*   An OpenAI API key, exported as `OPEN_AI_API_KEY`
*   Docker and Docker Compose

### Configuration

`src/main/resources/application.properties` already contains the defaults:

```properties
spring.ai.openai.api-key=${OPEN_AI_API_KEY}
spring.ai.openai.embedding.model=text-embedding-3-small
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.ai.vectorstore.pgvector.dimensions=1536
```

`pgvector.dimensions` must match the embedding model (1536 for `text-embedding-3-small`). If you change the model,
clear the `vector_store` table and re-upload your documents.

### Running the Application

1.  Start PostgreSQL with pgvector:
    ```bash
    docker-compose up -d
    ```
2.  Run the Spring Boot application:
    ```bash
    export OPEN_AI_API_KEY=<YOUR_API_KEY>
    mvn spring-boot:run
    ```

The application will be available at `http://localhost:8080`. A ready-made request set is in
`Spring AI 2 basics.postman_collection.json`.

## API Endpoints

### ETL

*   `POST /etl/store`: `multipart/form-data` with one part, `file`: the document (PDF or text). Spring Boot's default
    upload limit is 1 MB; raise `spring.servlet.multipart.max-file-size` for larger files.

### Chat

*   `POST /chat/text`: Ask a question answered from the stored documents.
    ```json
    { "message": "...", "conversationId": "1" }
    ```
