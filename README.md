# Spring AI 2 Basics with OpenAI

This project demonstrates the capabilities of the Spring AI framework, showcasing how to integrate and utilize OpenAI's models for various AI-powered tasks.

## Features

This project includes examples of the following Spring AI features:

*   **Chat Completion:** Engage in conversational AI using Open AI chat models. The application supports maintaining
    conversation history in a PostgreSQL database using `JdbcChatMemoryRepository`.
*   **Structured Output:** Convert natural language into structured data (POJOs), for example, to get a list of movie
    recommendations in a specific format.
*   **Image Generation:** Dynamically generate images from text prompts using the DALL-E model.
*   **Image Comprehension:** Analyze and describe images provided via URL, local path, or direct upload.
*   **Audio Transcription:** Convert audio files (MP4) into text.
*   **Text-to-Speech:** Generate audio (MPEG) from text.

## Getting Started

### Prerequisites

*   Java 25
*   Maven
*   An OpenAI API key
*   Docker and Docker Compose


### Configuration

1.  Add your OpenAI API key to the `src/main/resources/application.properties` file:

    ```properties
    spring.ai.openai.api-key=<YOUR_API_KEY>
    spring.ai.openai.chat.options.model=gpt-4o
    spring.ai.openai.image.options.model=dall-e-3
    spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    spring.jpa.hibernate.ddl-auto=create
    ```

### Running the Application

1.  Start the PostgreSQL database using Docker Compose:
    ```bash
    docker-compose up -d
    ```

2.  Build the project using Maven:
    ```bash
    mvn clean install
    ```
3.  Run the Spring Boot application:
    ```bash
    mvn spring-boot:run
    ```

The application will be available at `http://localhost:8080`.

## API Endpoints

### Chat

*   `POST /chat/text`: Get a text completion from the model.
*   `POST /chat/movieRecommendation`: Get a structured movie recommendation based on a text prompt.

### Image

*   `POST /image/generateImage`: Generate an image from a text prompt.
*   `POST /image/textWithImageUrl`: Analyze an image from a URL.
*   `POST /image/textWithImagePath`: Analyze an image from a local path.
*   `POST /image/textWithImage`: Analyze an uploaded image.

### Audio
*   `POST /audio/transcribe`: Transcribe an audio file.
*   `POST /audio/generateAudio`: Generate an audio file from text.
