package com.community.ai.rag.config;

import com.community.ai.rag.load.PgVectorDocumentLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * @Author 王哈哈
 * @Date 2025/10/13 15:42:05
 * @Description
 */
@Configuration
@Slf4j
public class VectorStoreConfig {

    @Value("${spring.ai.vectorstore.pgvector.max-document-batch-size:10}")
    private int batchSize;

    @Resource
    private JdbcTemplate vectorJdbcTemplate;

    @Resource
    private OpenAiEmbeddingModel embeddingModel;

    @Resource
    private PgVectorDocumentLoader pgVectorDocumentLoader;


    @Bean(name = "myPgVectorStore")
    VectorStore myPgVectorStore() {

        PgVectorStore pgVectorStore = PgVectorStore.builder(vectorJdbcTemplate, embeddingModel).build();

        // 加载文档
        List<Document> documents = pgVectorDocumentLoader.documentLoad();

        // 检查数据库是否存在数据
        Long count = vectorJdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Long.class);

        log.info("使用批量大小: {}", batchSize);

        // 根据数据库状态决定是否加载文档（避免重复添加）
        if (count == null || count == 0L) {
            // 空库，初次加载
            addDocumentsInBatches(pgVectorStore, documents, batchSize);
            log.info("已添加{}个文档到向量数据库", documents.size());
        } else if (!count.equals((long) documents.size())) {
            // 文档数量不匹配，清空并重新加载
            vectorJdbcTemplate.execute("TRUNCATE TABLE vector_store");
            log.info("已清空向量数据库并重新添加{}个文档", documents.size());
            addDocumentsInBatches(pgVectorStore, documents, batchSize);
        } else {
            // 数据库已存在数据，不进行操作
            log.info("知识库 信息未被修改,无需修改数据库");
        }
        return pgVectorStore;
    }

    /**
     * 分批添加文档到向量存储
     */
    private void addDocumentsInBatches(PgVectorStore vectorStore, List<Document> documents, int batchSize) {
        for (int i = 0; i < documents.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, documents.size());
            List<Document> batch = documents.subList(i, endIndex);
            vectorStore.add(batch);
            log.debug("已添加文档批次 {}/{}", (i / batchSize) + 1, (documents.size() + batchSize - 1) / batchSize);
        }
    }
}
