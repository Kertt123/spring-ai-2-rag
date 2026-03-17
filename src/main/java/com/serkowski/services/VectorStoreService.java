package com.serkowski.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;

import java.util.List;

public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void storeAsVector(Resource file) {
        List<Document> documents;
        if (file.getFilename().toLowerCase().endsWith(".pdf")) {
            TikaDocumentReader pdfReader = new TikaDocumentReader(file);
            documents = pdfReader.get();
            documents.forEach(document -> document.getMetadata().put("filename", file.getFilename()));
        } else {
            TextReader textReader = new TextReader(file);
            textReader.getCustomMetadata().put("filename", file.getFilename());
            documents = textReader.get();
        }
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(1000)
                .withMinChunkSizeChars(400)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();

        List<Document> splitDocuments = tokenTextSplitter.apply(documents);

        vectorStore.accept(splitDocuments);
    }
}
