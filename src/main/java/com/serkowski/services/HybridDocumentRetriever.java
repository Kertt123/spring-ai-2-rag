package com.serkowski.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.ai.vectorstore.pgvector.PgVectorFilterExpressionConverter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Hybrid search = vector search (meaning) + keyword search (exact words), merged into one ranking.
 * Keywords catch what embeddings miss: error codes, model numbers, exact names.
 */
public class HybridDocumentRetriever implements DocumentRetriever {

    private static final Logger log = LoggerFactory.getLogger(HybridDocumentRetriever.class);
    private static final int RRF_K = 60; // standard constant of Reciprocal Rank Fusion

    private final DocumentRetriever vectorRetriever;
    private final JdbcTemplate jdbcTemplate;
    private final int topK;

    public HybridDocumentRetriever(DocumentRetriever vectorRetriever, JdbcTemplate jdbcTemplate, int topK) {
        this.vectorRetriever = vectorRetriever;
        this.jdbcTemplate = jdbcTemplate;
        this.topK = topK;
        // index for the keyword search below, otherwise every query scans the whole table
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS vector_store_content_fts "
                + "ON vector_store USING GIN (to_tsvector('english', content))");
    }

    @Override
    public List<Document> retrieve(Query query) {
        List<Document> vectorHits = vectorRetriever.retrieve(query);
        List<Document> keywordHits = keywordSearch(query);

        log.info("Hybrid '{}': vector={} keyword={}", query.text(),
                vectorHits.stream().map(Document::getId).toList(),
                keywordHits.stream().map(Document::getId).toList());

        return fuse(vectorHits, keywordHits);
    }

    /** Postgres full-text search on the chunk text, restricted by the same type/tags filter as the vector search. */
    private List<Document> keywordSearch(Query query) {
        String words = query.text().trim().replaceAll("\\s+", " or "); // any word may match, not all of them

        String sql = """
                SELECT id::text, content FROM vector_store
                WHERE to_tsvector('english', content) @@ websearch_to_tsquery('english', ?)
                  AND metadata::jsonb @@ ?::jsonpath
                ORDER BY ts_rank_cd(to_tsvector('english', content), websearch_to_tsquery('english', ?)) DESC
                LIMIT ?
                """;
        String filter = (String) query.context().get(VectorStoreDocumentRetriever.FILTER_EXPRESSION);
        String jsonPath = new PgVectorFilterExpressionConverter()
                .convertExpression(new FilterExpressionTextParser().parse(filter));

        return jdbcTemplate.query(sql,
                (rs, i) -> Document.builder().id(rs.getString(1)).text(rs.getString(2)).build(),
                words, jsonPath, words, topK);
    }

    /** Reciprocal Rank Fusion: each list gives a document 1/(60+rank) points, the sums decide the order. */
    private List<Document> fuse(List<Document> vectorHits, List<Document> keywordHits) {
        Map<String, Document> documents = new LinkedHashMap<>();
        Map<String, Double> points = new HashMap<>();

        for (List<Document> hits : List.of(vectorHits, keywordHits)) {
            for (int rank = 0; rank < hits.size(); rank++) {
                Document doc = hits.get(rank);
                documents.putIfAbsent(doc.getId(), doc); // vector copy comes first and has the metadata
                points.merge(doc.getId(), 1.0 / (RRF_K + rank + 1), Double::sum);
            }
        }

        return new ArrayList<>(documents.values()).stream()
                .sorted(Comparator.comparingDouble((Document d) -> points.get(d.getId())).reversed())
                .limit(topK)
                .toList();
    }
}
