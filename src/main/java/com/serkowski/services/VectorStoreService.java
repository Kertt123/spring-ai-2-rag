package com.serkowski.services;

import com.serkowski.model.MetadataKeyword;
import com.serkowski.model.Tag;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;

import java.util.List;

public class VectorStoreService {

    private final VectorStore vectorStore;
    private final KeywordMetadataEnricher keywordMetadataEnricher;

    public VectorStoreService(VectorStore vectorStore, KeywordMetadataEnricher keywordMetadataEnricher) {
        this.vectorStore = vectorStore;
        this.keywordMetadataEnricher = keywordMetadataEnricher;
    }

    public void storeAsVector(Resource file, List<MetadataKeyword> keywords) {
        List<Document> documents = getDocuments(file, keywords);
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(1000)
                .withMinChunkSizeChars(400)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();

        List<Document> splitDocuments = tokenTextSplitter.apply(documents);
        List<Document> enrichedDocuments = keywordMetadataEnricher.apply(splitDocuments);

        // the enricher yields one comma-separated string, which can't be filtered; a list can (tags in [...])
        enrichedDocuments.forEach(d -> d.getMetadata().put("tags", Tag.parse((String) d.getMetadata().get("excerpt_keywords"))));

        vectorStore.accept(enrichedDocuments);
    }

    private static @NonNull List<Document> getDocuments(Resource file, List<MetadataKeyword> keywords) {
        List<Document> documents;
        if (file.getFilename().toLowerCase().endsWith(".pdf")) {
            TikaDocumentReader pdfReader = new TikaDocumentReader(file);
            documents = pdfReader.get();

        } else {
            TextReader textReader = new TextReader(file);
            documents = textReader.get();
        }
        documents.forEach(document -> {
            document.getMetadata().put("filename", file.getFilename());
            keywords.forEach(keyword -> document.getMetadata().put(keyword.keyword(), keyword.value()));
        });
        return documents;
    }
}
