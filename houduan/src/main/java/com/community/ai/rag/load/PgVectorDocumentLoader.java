package com.community.ai.rag.load;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PgVectorDocumentLoader {

    private static final Pattern FIRST_LEVEL_HEADING_PATTERN = Pattern.compile("(?m)^#\\s+(.+?)\\s*$");
    private static final Pattern SECOND_LEVEL_HEADING_PATTERN = Pattern.compile("(?m)^##\\s+(.+?)\\s*$");
    private static final Pattern LEADING_ORDER_PATTERN = Pattern.compile(
            "^\\s*(\\u7B2C?[\\u4E00\\u4E8C\\u4E09\\u56DB\\u4E94\\u516D\\u4E03\\u516B\\u4E5D\\u5341\\u767E\\u5343\\u4E07\\u4E24\\u96F6\\u3007\\d]+[\\u3001\\.\\uFF0E,\\uFF0C\\)\\uFF09]\\s*)+"
    );

    @jakarta.annotation.Resource
    private ResourcePatternResolver resourcePatternResolver;

    public List<Document> documentLoad() {
        LinkedList<Document> allDocuments = new LinkedList<>();
        try {
            org.springframework.core.io.Resource[] resources = resourcePatternResolver.getResources("classpath:doc/*.md");
            for (org.springframework.core.io.Resource resource : resources) {
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withIncludeBlockquote(false)
                        .withIncludeCodeBlock(false)
                        .withHorizontalRuleCreateDocument(true)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                List<Document> documents = reader.get();

                String documentTitle = extractDocumentTitleFromResource(resource);
                for (Document document : documents) {
                    String sectionTitle = extractSectionTitleFromText(document.getText());
                    if (sectionTitle == null || sectionTitle.isBlank()) {
                        sectionTitle = documentTitle;
                    }
                    if (documentTitle != null && !documentTitle.isBlank()) {
                        document.getMetadata().put("documentTitle", documentTitle);
                    }
                    if (sectionTitle != null && !sectionTitle.isBlank()) {
                        document.getMetadata().put("sectionTitle", sectionTitle);
                    }
                }
                allDocuments.addAll(documents);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load markdown docs", e);
        }
        return allDocuments;
    }

    private String extractDocumentTitleFromResource(org.springframework.core.io.Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return extractDocumentTitleFromText(markdown);
        }
    }

    private String extractDocumentTitleFromText(String markdownText) {
        if (markdownText == null || markdownText.isBlank()) {
            return null;
        }
        String title = matchHeading(markdownText, FIRST_LEVEL_HEADING_PATTERN);
        if (title == null || title.isBlank()) {
            title = matchHeading(markdownText, SECOND_LEVEL_HEADING_PATTERN);
        }
        return normalizeTitle(title);
    }

    private String extractSectionTitleFromText(String markdownText) {
        if (markdownText == null || markdownText.isBlank()) {
            return null;
        }
        String title = matchHeading(markdownText, SECOND_LEVEL_HEADING_PATTERN);
        if (title == null || title.isBlank()) {
            title = matchHeading(markdownText, FIRST_LEVEL_HEADING_PATTERN);
        }
        return normalizeTitle(title);
    }

    private String matchHeading(String markdownText, Pattern pattern) {
        Matcher matcher = pattern.matcher(markdownText);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String normalizeTitle(String rawTitle) {
        if (rawTitle == null) {
            return null;
        }
        String normalized = rawTitle
                .replace("**", "")
                .replace("__", "")
                .trim();
        normalized = LEADING_ORDER_PATTERN.matcher(normalized).replaceFirst("").trim();
        return normalized.isBlank() ? null : normalized;
    }
}
