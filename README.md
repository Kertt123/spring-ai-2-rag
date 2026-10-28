# Spring AI 2 RAG with OpenAI

This project demonstrates Retrieval-Augmented Generation (RAG) with Spring AI 2 and OpenAI. Documents are embedded and
stored in PostgreSQL (pgvector), then used as context when answering chat questions.

## Features

*   **Chat Completion:** Conversational AI using OpenAI chat models. Conversation history is kept in PostgreSQL via
    `JdbcChatMemoryRepository`, keyed by `conversationId`.
*   **RAG:** Chat answers are grounded in stored documents retrieved from the pgvector store (vector store advisor).
*   **ETL:** Upload a file; it is split into chunks, embedded (`text-embedding-3-small`) and stored as vectors.

## Getting Started

### Prerequisites

*   Java 25
*   Maven
*   An OpenAI API key
*   Docker and Docker Compose

### Configuration

Set your OpenAI API key as an environment variable (read by `src/main/resources/application.properties`):

```bash
export OPEN_AI_API_KEY=<YOUR_API_KEY>
```

Other settings in `application.properties`:

```properties
spring.ai.openai.api-key=${OPEN_AI_API_KEY}
spring.ai.openai.embedding.model=text-embedding-3-small
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.ai.vectorstore.pgvector.dimensions=1536
```

### Running the Application

1.  Start PostgreSQL with pgvector:
    ```bash
    docker-compose up -d
    ```
2.  Run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```

The application will be available at `http://localhost:8080`.

## API Endpoints

*   `POST /etl/store`: Upload a file (multipart form field `file`) to embed and store it in the vector store.
*   `POST /chat/text`: Ask a question. JSON body: `{"message": "...", "conversationId": "..."}`.

A ready-made request set is in `Spring AI 2 basics.postman_collection.json`.
